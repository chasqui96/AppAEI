import * as SQLite from 'expo-sqlite';

import { categorize, normalizeComercioKey } from '../categorize/categorize';
import { ExtractoParseado } from '../parsers/types';
import { SCHEMA_SQL } from './schema';

let db: SQLite.SQLiteDatabase | null = null;

export function getDb(): SQLite.SQLiteDatabase {
  if (!db) {
    db = SQLite.openDatabaseSync('chasfinapp.db');
    db.execSync(SCHEMA_SQL);
  }
  return db;
}

export function getLearnedRules(): Record<string, string> {
  const rows = getDb().getAllSync<{ keyword: string; categoria: string }>(
    'SELECT keyword, categoria FROM categorias'
  );
  const rules: Record<string, string> = {};
  for (const row of rows) {
    rules[row.keyword] = row.categoria;
  }
  return rules;
}

/** Persists a manual re-categorization as a learned rule and re-applies it to
 * every other transaccion already stored for that same merchant key. */
export function setCategoriaTransaccion(transaccionId: number, comercio: string, categoria: string): void {
  const keyword = normalizeComercioKey(comercio);
  const conn = getDb();
  conn.withTransactionSync(() => {
    conn.runSync('INSERT OR REPLACE INTO categorias (keyword, categoria) VALUES (?, ?)', [keyword, categoria]);
    conn.runSync('UPDATE transacciones SET categoria = ? WHERE id = ?', [categoria, transaccionId]);
    conn.runSync('UPDATE transacciones SET categoria = ? WHERE comercio LIKE ?', [categoria, `%${comercio}%`]);
  });
}

function getOrCreateTarjeta(banco: string, alias: string): number {
  const conn = getDb();
  conn.runSync('INSERT OR IGNORE INTO tarjetas (banco, alias) VALUES (?, ?)', [banco, alias]);
  const row = conn.getFirstSync<{ id: number }>('SELECT id FROM tarjetas WHERE banco = ? AND alias = ?', [
    banco,
    alias,
  ]);
  if (!row) throw new Error('Failed to create tarjeta');
  return row.id;
}

/** Inserts a parsed extracto (and its transactions) for a card, replacing any
 * existing data for the same periodo so re-importing a statement is idempotent. */
export function guardarExtracto(extracto: ExtractoParseado, alias: string, periodo: string): void {
  const conn = getDb();
  const learnedRules = getLearnedRules();
  const tarjetaId = getOrCreateTarjeta(extracto.banco, alias);
  const { resumen } = extracto;

  conn.withTransactionSync(() => {
    const existing = conn.getFirstSync<{ id: number }>(
      'SELECT id FROM resumenes_mensuales WHERE tarjeta_id = ? AND periodo = ?',
      [tarjetaId, periodo]
    );
    if (existing) {
      conn.runSync('DELETE FROM transacciones WHERE resumen_id = ?', [existing.id]);
      conn.runSync('DELETE FROM resumenes_mensuales WHERE id = ?', [existing.id]);
    }

    const result = conn.runSync(
      `INSERT INTO resumenes_mensuales
        (tarjeta_id, periodo, deuda_anterior, pagos, saldo_financiado, compras_del_mes,
         deuda_total_periodo, deuda_cuotas_facturar, deuda_total, pago_minimo, fecha_vencimiento, tan, tae)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        tarjetaId,
        periodo,
        resumen.deudaAnterior,
        resumen.pagos,
        resumen.saldoFinanciado,
        resumen.comprasDelMes,
        resumen.deudaTotalPeriodo,
        resumen.deudaCuotasFacturar,
        resumen.deudaTotal,
        resumen.pagoMinimo,
        resumen.fechaVencimiento,
        resumen.tan,
        resumen.tae,
      ]
    );
    const resumenId = result.lastInsertRowId;

    for (const tx of extracto.transacciones) {
      const categoria = categorize(tx.comercio, tx.esPago, learnedRules);
      conn.runSync(
        'INSERT INTO transacciones (resumen_id, fecha, comercio, monto, es_pago, categoria) VALUES (?, ?, ?, ?, ?, ?)',
        [resumenId, tx.fecha, tx.comercio, tx.monto, tx.esPago ? 1 : 0, categoria]
      );
    }
  });
}

export interface TarjetaConResumen {
  tarjetaId: number;
  banco: string;
  alias: string;
  resumenId: number;
  comprasDelMes: number | null;
  deudaTotal: number | null;
  pagoMinimo: number | null;
  fechaVencimiento: string | null;
}

export function getResumenesPorPeriodo(periodo: string): TarjetaConResumen[] {
  return getDb().getAllSync<TarjetaConResumen>(
    `SELECT t.id as tarjetaId, t.banco as banco, t.alias as alias, r.id as resumenId,
            r.compras_del_mes as comprasDelMes, r.deuda_total as deudaTotal,
            r.pago_minimo as pagoMinimo, r.fecha_vencimiento as fechaVencimiento
     FROM resumenes_mensuales r
     JOIN tarjetas t ON t.id = r.tarjeta_id
     WHERE r.periodo = ?`,
    [periodo]
  );
}

export function getPeriodosDisponibles(): string[] {
  const rows = getDb().getAllSync<{ periodo: string }>(
    'SELECT DISTINCT periodo FROM resumenes_mensuales ORDER BY periodo DESC'
  );
  return rows.map((r) => r.periodo);
}

export interface CategoriaTotal {
  categoria: string;
  total: number;
}

export function getTotalesPorCategoria(periodo: string): CategoriaTotal[] {
  return getDb().getAllSync<CategoriaTotal>(
    `SELECT tx.categoria as categoria, SUM(tx.monto) as total
     FROM transacciones tx
     JOIN resumenes_mensuales r ON r.id = tx.resumen_id
     WHERE r.periodo = ? AND tx.es_pago = 0
     GROUP BY tx.categoria
     ORDER BY total DESC`,
    [periodo]
  );
}

export interface TendenciaMensual {
  periodo: string;
  total: number;
}

export function getTendenciaMensual(): TendenciaMensual[] {
  return getDb().getAllSync<TendenciaMensual>(
    `SELECT r.periodo as periodo, SUM(tx.monto) as total
     FROM transacciones tx
     JOIN resumenes_mensuales r ON r.id = tx.resumen_id
     WHERE tx.es_pago = 0
     GROUP BY r.periodo
     ORDER BY r.periodo ASC`
  );
}

export interface TransaccionRow {
  id: number;
  fecha: string;
  comercio: string;
  monto: number;
  esPago: number;
  categoria: string;
  banco: string;
  alias: string;
}

export function getTransacciones(periodo?: string, busqueda?: string): TransaccionRow[] {
  const conditions: string[] = [];
  const params: string[] = [];
  if (periodo) {
    conditions.push('r.periodo = ?');
    params.push(periodo);
  }
  if (busqueda) {
    conditions.push('tx.comercio LIKE ?');
    params.push(`%${busqueda}%`);
  }
  const where = conditions.length ? `WHERE ${conditions.join(' AND ')}` : '';

  return getDb().getAllSync<TransaccionRow>(
    `SELECT tx.id as id, tx.fecha as fecha, tx.comercio as comercio, tx.monto as monto,
            tx.es_pago as esPago, tx.categoria as categoria, t.banco as banco, t.alias as alias
     FROM transacciones tx
     JOIN resumenes_mensuales r ON r.id = tx.resumen_id
     JOIN tarjetas t ON t.id = r.tarjeta_id
     ${where}
     ORDER BY tx.fecha DESC`,
    params
  );
}
