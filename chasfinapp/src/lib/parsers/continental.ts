import { ExtractoParseado, ResumenFinanciero, Transaccion } from './types';

const TX_START = /^(\d{2}\/\d{2}\/\d{2,4})\s+(\d{2}\/\d{2}\/\d{2,4})\s+(\S+)\s+(.+)$/;

function parseAmount(raw: string): number {
  const negative = raw.trim().startsWith('-');
  const cleaned = raw.replace(/[^\d,.\-]/g, '');
  const normalized = cleaned.includes(',')
    ? cleaned.replace(/\./g, '').replace(',', '.')
    : cleaned.replace(/\./g, '');
  const value = Math.abs(parseFloat(normalized));
  return negative ? -value : value;
}

function toIsoDate(ddmmyy: string): string {
  const [d, m, y] = ddmmyy.split('/');
  const year = y.length === 2 ? `20${y}` : y;
  return `${year}-${m.padStart(2, '0')}-${d.padStart(2, '0')}`;
}

function lastNumberToken(line: string): { value: number; index: number } | null {
  const match = line.match(/(-?\d{1,3}(?:\.\d{3})*)\s*$/);
  if (!match) return null;
  return { value: parseAmount(match[1]), index: match.index ?? line.length };
}

function parseTransactionLine(fecha: string, rest: string): { comercio: string; monto: number | null; esPago: boolean } {
  let body = rest.trim();
  let esPago = false;

  if (/\sCR$/.test(body)) {
    esPago = true;
    body = body.replace(/\sCR$/, '').trim();
  }

  const montoMatch = lastNumberToken(body);
  let monto: number | null = null;
  if (montoMatch) {
    monto = montoMatch.value;
    body = body.slice(0, montoMatch.index).trim();
  }

  body = body.replace(/\s\d+%\s*$/, '').trim();
  body = body.replace(/\s(SI|NO)$/, '').trim();

  return { comercio: body, monto, esPago };
}

export function parseContinentalTransacciones(lines: string[]): Transaccion[] {
  const transacciones: Transaccion[] = [];
  let pending: Transaccion | null = null;

  for (const rawLine of lines) {
    const line = rawLine.trim();
    const match = line.match(TX_START);

    if (match) {
      pending = null;
      const [, fechaOperacion, , , rest] = match;
      const { comercio, monto, esPago } = parseTransactionLine(fechaOperacion, rest);
      if (!comercio) continue;

      const tx: Transaccion = {
        fecha: toIsoDate(fechaOperacion),
        comercio,
        monto: monto ?? 0,
        esPago,
      };

      if (monto === null) {
        pending = tx;
      } else {
        transacciones.push(tx);
      }
      continue;
    }

    if (pending) {
      const found = lastNumberToken(line);
      if (found) {
        pending.monto = found.value;
        transacciones.push(pending);
      }
      pending = null;
    }
  }

  return transacciones;
}

function findValueAfterLabel(lines: string[], labelMatch: (line: string) => boolean): number | null {
  const idx = lines.findIndex(labelMatch);
  if (idx === -1 || idx + 1 >= lines.length) return null;
  const found = lastNumberToken(lines[idx + 1]);
  return found ? found.value : null;
}

function findValueOnLabelLine(lines: string[], regex: RegExp): number | null {
  for (const line of lines) {
    const match = line.match(regex);
    if (match) return parseAmount(match[1]);
  }
  return null;
}

export function parseContinentalResumen(lines: string[]): ResumenFinanciero {
  const deudaAnterior = findValueAfterLabel(lines, (l) => l.includes('Deuda Anterior'));
  const pagos = findValueAfterLabel(lines, (l) => /\(-\)\s*Pagos/.test(l));
  const saldoFinanciado = findValueAfterLabel(lines, (l) => l.includes('Saldo Financiado'));
  const comprasDelMes = findValueAfterLabel(lines, (l) => /Compra.*cargos del mes/.test(l));
  const deudaTotalPeriodo = findValueAfterLabel(lines, (l) => l.includes('DEUDA TOTAL DEL PERIODO'));
  const deudaCuotasFacturar = findValueAfterLabel(lines, (l) => l.includes('Deuda en Cuotas a facturar'));
  const deudaTotal = findValueAfterLabel(
    lines,
    (l) => l.includes('DEUDA TOTAL') && !l.includes('PERIODO')
  );

  const pagoMinimoLineIdx = lines.findIndex((l) => l.includes('Pago mínimo'));
  let pagoMinimo: number | null = null;
  if (pagoMinimoLineIdx !== -1 && pagoMinimoLineIdx + 1 < lines.length) {
    const found = lastNumberToken(lines[pagoMinimoLineIdx + 1]);
    pagoMinimo = found ? found.value : null;
  }

  let fechaVencimiento: string | null = null;
  for (const line of lines) {
    const match = line.match(/Actual\s+(\d{2}\/\d{2}\/\d{2,4})\s+(\d{2}\/\d{2}\/\d{2,4})/);
    if (match) {
      fechaVencimiento = toIsoDate(match[2]);
      break;
    }
  }

  const tan = findValueOnLabelLine(lines, /T\.A\.N[^0-9]*(\d+,\d+)/);
  const tae = findValueOnLabelLine(lines, /T\.A\.E[^0-9]*(\d+,\d+)/);

  return {
    deudaAnterior,
    pagos,
    saldoFinanciado,
    comprasDelMes,
    deudaTotalPeriodo,
    deudaCuotasFacturar,
    deudaTotal,
    pagoMinimo,
    fechaVencimiento,
    tan,
    tae,
  };
}

export function parseContinental(lines: string[]): ExtractoParseado {
  return {
    banco: 'continental',
    transacciones: parseContinentalTransacciones(lines),
    resumen: parseContinentalResumen(lines),
  };
}
