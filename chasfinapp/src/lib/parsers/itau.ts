import { findRateNearLabel, lastNumberTokenDot, parseAmountDot, toIsoDate } from './common';
import { ExtractoParseado, ResumenFinanciero, Transaccion } from './types';

const TX_START = /^(\d{2}\/\d{2}\/\d{2,4})\s+(\d{2}\/\d{2}\/\d{2,4})\s+(\S+)\s+(.+)$/;
const SECTION_PAGOS = /^PAGOS$/i;

function parseTransactionLine(rest: string, inPagosSection: boolean): { comercio: string; monto: number | null; esPago: boolean } {
  let body = rest.trim();

  const montoMatch = lastNumberTokenDot(body);
  let monto: number | null = null;
  if (montoMatch) {
    monto = montoMatch.value;
    body = body.slice(0, montoMatch.index).trim();
  }

  body = body.replace(/\s\d+%\s*$/, '').trim();
  body = body.replace(/\s(SI|NO)$/, '').trim();

  const esPago = inPagosSection && monto !== null && monto < 0;
  return { comercio: body, monto, esPago };
}

export function parseItauTransacciones(lines: string[]): Transaccion[] {
  const transacciones: Transaccion[] = [];
  let pending: Transaccion | null = null;
  let inPagosSection = false;

  for (const rawLine of lines) {
    const line = rawLine.trim();

    if (SECTION_PAGOS.test(line)) {
      inPagosSection = true;
      pending = null;
      continue;
    }
    if (/^COMPRAS/i.test(line)) {
      inPagosSection = false;
      pending = null;
      continue;
    }

    const match = line.match(TX_START);

    if (match) {
      pending = null;
      const [, fechaOperacion, , , rest] = match;
      const { comercio, monto, esPago } = parseTransactionLine(rest, inPagosSection);
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

export function parseItauResumen(lines: string[]): ResumenFinanciero {
  let deudaAnterior: number | null = null;
  let fechaVencimiento: string | null = null;
  for (const line of lines) {
    // The vencimiento date sits on the same line as "Deuda Anterior" in Itaú's box layout.
    const match = line.match(/(\d{2}\/\d{2}\/\d{4})\s*Deuda Anterior\s+([\d.,]+)/);
    if (match) {
      fechaVencimiento = toIsoDate(match[1]);
      deudaAnterior = parseAmountDot(match[2]);
      break;
    }
  }

  let pagos: number | null = null;
  for (const line of lines) {
    const match = line.match(/\(-\)\s*Pagos\s+(-?[\d.,]+)/);
    if (match) {
      pagos = parseAmountDot(match[1]);
      break;
    }
  }

  let saldoFinanciado: number | null = null;
  for (const line of lines) {
    const match = line.match(/Saldo Financiado\s+(-?[\d.,]+)/);
    if (match) {
      saldoFinanciado = parseAmountDot(match[1]);
      break;
    }
  }

  let comprasDelMes: number | null = null;
  for (const line of lines) {
    const match = line.match(/Compras?\s+y?\s*cargos del mes\s+(-?[\d.,]+)/i);
    if (match) {
      comprasDelMes = parseAmountDot(match[1]);
      break;
    }
  }

  let deudaTotalPeriodo: number | null = null;
  const deudaTotalPeriodoIdx = lines.findIndex((l) => /\(=\)\s*DEUDA TOTAL DEL PERIODO/i.test(l));
  if (deudaTotalPeriodoIdx !== -1 && deudaTotalPeriodoIdx + 1 < lines.length) {
    const found = lastNumberTokenDot(lines[deudaTotalPeriodoIdx + 1]);
    deudaTotalPeriodo = found ? found.value : null;
  }

  let deudaCuotasFacturar: number | null = null;
  for (const line of lines) {
    const match = line.match(/Deuda en cuotas a facturar\s+(-?[\d.,]+)/i);
    if (match) {
      deudaCuotasFacturar = parseAmountDot(match[1]);
      break;
    }
  }

  let deudaTotal: number | null = null;
  for (const line of lines) {
    const match = line.match(/\(=\)\s*DEUDA TOTAL\s+(-?[\d.,]+)/i);
    if (match && !/PERIODO/i.test(line)) {
      deudaTotal = parseAmountDot(match[1]);
      break;
    }
  }

  const pagoMinimoLineIdx = lines.findIndex((l) => l.includes('Compras no Financiab'));
  let pagoMinimo: number | null = null;
  if (pagoMinimoLineIdx !== -1 && pagoMinimoLineIdx + 1 < lines.length) {
    const found = lastNumberTokenDot(lines[pagoMinimoLineIdx + 1]);
    pagoMinimo = found ? found.value : null;
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

export function parseItau(lines: string[]): ExtractoParseado {
  return {
    banco: 'itau',
    transacciones: parseItauTransacciones(lines),
    resumen: parseItauResumen(lines),
  };
}
