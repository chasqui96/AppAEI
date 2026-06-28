import { findRateNearLabel, findResumenValue, lastNumberTokenComma, toIsoDate } from './common';
import { ExtractoParseado, ResumenFinanciero, Transaccion } from './types';

const TX_START = /^(\d{2}\/\d{2}\/\d{4})\s+(\d{2}\/\d{2}\/\d{4})\s+(.+)$/;

function parseTransactionLine(rest: string): { comercio: string; monto: number | null; esPago: boolean } {
  let body = rest.trim().replace(/^\d{5,}\s+/, ''); // strip leading cupón when present

  const montoMatch = lastNumberTokenComma(body);
  let monto: number | null = null;
  if (montoMatch) {
    monto = montoMatch.value;
    body = body.slice(0, montoMatch.index).trim();
  }

  body = body.replace(/\s\d+\s*$/, '').trim(); // strip bare I.V.A. column when present
  body = body.replace(/\s(N|S)$/, '').trim(); // strip FIN. column

  const comercio = body.trim();
  const esPago = /^SU PAGO/i.test(comercio);
  return { comercio, monto, esPago };
}

export function parseUenoTransacciones(lines: string[]): Transaccion[] {
  const transacciones: Transaccion[] = [];
  let pending: Transaccion | null = null;

  for (const rawLine of lines) {
    const line = rawLine.trim();
    const match = line.match(TX_START);

    if (match) {
      pending = null;
      const [, fechaOperacion, , rest] = match;
      const { comercio, monto, esPago } = parseTransactionLine(rest);
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
      const found = lastNumberTokenComma(line);
      if (found) {
        pending.monto = found.value;
        transacciones.push(pending);
      }
      pending = null;
    }
  }

  return transacciones;
}

export function parseUenoResumen(lines: string[]): ResumenFinanciero {
  const deudaAnterior = findResumenValue(lines, (l) => l.includes('Deuda Anterior'), lastNumberTokenComma);
  const pagos = findResumenValue(lines, (l) => /\(-\)\s*Pagos/.test(l), lastNumberTokenComma);
  const saldoFinanciado = findResumenValue(lines, (l) => l.includes('Saldo Financiado'), lastNumberTokenComma);
  const comprasDelMes = findResumenValue(
    lines,
    (l) => /Compras?\s+y?\s*cargos del mes/i.test(l),
    lastNumberTokenComma
  );
  const deudaTotalPeriodo = findResumenValue(
    lines,
    (l) => /Deuda total del periodo/i.test(l),
    lastNumberTokenComma
  );
  const deudaCuotasFacturar = findResumenValue(
    lines,
    (l) => l.includes('Deuda en cuotas a facturar'),
    lastNumberTokenComma
  );
  const deudaTotal = findResumenValue(
    lines,
    (l) => /\(=\)\s*Deuda total\b/i.test(l) && !/periodo/i.test(l),
    lastNumberTokenComma
  );

  const pagoMinimoLineIdx = lines.findIndex((l) => l.includes('Compras no Financiab'));
  let pagoMinimo: number | null = null;
  if (pagoMinimoLineIdx !== -1 && pagoMinimoLineIdx + 1 < lines.length) {
    const found = lastNumberTokenComma(lines[pagoMinimoLineIdx + 1]);
    pagoMinimo = found ? found.value : null;
  }

  let fechaVencimiento: string | null = null;
  for (const line of lines) {
    const match = line.match(/Actual\s+(\d{2}\/\d{2}\/\d{4})\s+(\d{2}\/\d{2}\/\d{4})/);
    if (match) {
      fechaVencimiento = toIsoDate(match[2]);
      break;
    }
  }

  const tan = findRateNearLabel(lines, /T\.A\.N/);
  const tae = findRateNearLabel(lines, /T\.A\.E/);

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

export function parseUeno(lines: string[]): ExtractoParseado {
  return {
    banco: 'ueno',
    transacciones: parseUenoTransacciones(lines),
    resumen: parseUenoResumen(lines),
  };
}
