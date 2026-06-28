export const SCHEMA_SQL = `
CREATE TABLE IF NOT EXISTS tarjetas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  banco TEXT NOT NULL,
  alias TEXT NOT NULL,
  UNIQUE(banco, alias)
);

CREATE TABLE IF NOT EXISTS resumenes_mensuales (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  tarjeta_id INTEGER NOT NULL REFERENCES tarjetas(id),
  periodo TEXT NOT NULL,
  deuda_anterior REAL,
  pagos REAL,
  saldo_financiado REAL,
  compras_del_mes REAL,
  deuda_total_periodo REAL,
  deuda_cuotas_facturar REAL,
  deuda_total REAL,
  pago_minimo REAL,
  fecha_vencimiento TEXT,
  tan REAL,
  tae REAL,
  UNIQUE(tarjeta_id, periodo)
);

CREATE TABLE IF NOT EXISTS transacciones (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  resumen_id INTEGER NOT NULL REFERENCES resumenes_mensuales(id),
  fecha TEXT NOT NULL,
  comercio TEXT NOT NULL,
  monto REAL NOT NULL,
  es_pago INTEGER NOT NULL,
  categoria TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS categorias (
  keyword TEXT PRIMARY KEY,
  categoria TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transacciones_resumen ON transacciones(resumen_id);
CREATE INDEX IF NOT EXISTS idx_resumenes_tarjeta ON resumenes_mensuales(tarjeta_id);
`;
