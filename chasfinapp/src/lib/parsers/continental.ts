import { findRateOnLabelLine, findResumenValue, lastNumberTokenDot, toIsoDate } from './common';
import { ExtractoParseado, ResumenFinanciero, Transaccion } from './types';

const TX_START = /^(\d{2}\/\d{2}\/\d{2,4})\s+(\d{2}\/\d{2}\/\d{2,4})\s+(\S+)\s+(.+)$/;

function parseTransactionLine(rest: string): { comercio: string; monto: number | null; esPago: boolean } {
  let body = rest.trim();
  let esPago = false;

  if (/\sCR$/.test(body)) {
    esPago = true;
    body = body.replace(/\sCR$/, '').trim();
  }

  const montoMatch = lastNumberTokenDot(body);
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
      const found = lastNumberTokenDot(line);
      if (found) {
        pending.monto = found.value;
        transacciones.push(pending);
      }
      pending = null;
    }
  }

  return transacciones;
}

export function parseContinentalResumen(lines: string[]): ResumenFinanciero {
  const deudaAnterior = findResumenValue(lines, (l) => l.includes('Deuda Anterior'), lastNumberTokenDot);
  const pagos = findResumenValue(lines, (l) => /\(-\)\s*Pagos/.test(l), lastNumberTokenDot);
  const saldoFinanciado = findResumenValue(lines, (l) => l.includes('Saldo Financiado'), lastNumberTokenDot);
  const comprasDelMes = findResumenValue(lines, (l) => /Compras?\s+y?\s*cargos del mes/i.test(l), lastNumberTokenDot);
  const deudaTotalPeriodo = findResumenValue(lines, (l) => l.includes('DEUDA TOTAL DEL PERIODO'), lastNumberTokenDot);
  const deudaCuotasFacturar = findResumenValue(
    lines,
    (l) => l.includes('Deuda en Cuotas a facturar'),
    lastNumberTokenDot
  );
  const deudaTotal = findResumenValue(
    lines,
    (l) => l.includes('DEUDA TOTAL') && !l.includes('PERIODO'),
    lastNumberTokenDot
  );

  const pagoMinimoLineIdx = lines.findIndex((l) => l.includes('Pago mínimo'));
  let pagoMinimo: number | null = null;
  if (pagoMinimoLineIdx !== -1 && pagoMinimoLineIdx + 1 < lines.length) {
    const found = lastNumberTokenDot(lines[pagoMinimoLineIdx + 1]);
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

  const tan = findRateOnLabelLine(lines, /T\.A\.N[^0-9]*(\d+,\d+)/);
  const tae = findRateOnLabelLine(lines, /T\.A\.E[^0-9]*(\d+,\d+)/);

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
