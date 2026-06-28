import fs from 'fs';
import path from 'path';

import { extractPdfLines } from '../../pdf/extractPdfLines';
import { parseUeno } from '../ueno';

describe('parseUeno', () => {
  it('parses transactions and the financial summary from a real Ueno extracto', async () => {
    const pdfPath = path.join(__dirname, '..', '__fixtures__', 'ueno-sample.pdf');
    const data = new Uint8Array(fs.readFileSync(pdfPath));
    const lines = await extractPdfLines(data);

    const { banco, transacciones, resumen } = parseUeno(lines);

    expect(banco).toBe('ueno');
    expect(transacciones.length).toBeGreaterThanOrEqual(35);

    const neusaJoyas = transacciones.find((t) => t.comercio.includes('NEUSA JOYAS'));
    expect(neusaJoyas).toEqual({
      fecha: '2026-01-21',
      comercio: 'NEUSA JOYAS 05/10',
      monto: 322000,
      esPago: false,
    });

    const petropar = transacciones.find((t) => t.comercio.includes('PETROPAR 10 DE AGOSTO'));
    expect(petropar?.monto).toBe(50000);
    expect(petropar?.esPago).toBe(false);

    const pago = transacciones.find((t) => t.esPago && t.monto === 125000);
    expect(pago?.comercio).toBe('SU PAGO, GRACIAS.');

    const samsung = transacciones.find((t) => t.comercio.includes('SAMSUNG ELECTRONIC'));
    expect(samsung?.monto).toBe(12298);

    expect(resumen).toEqual({
      deudaAnterior: 1869165,
      pagos: 6747582,
      saldoFinanciado: -4878417,
      comprasDelMes: 6129370,
      deudaTotalPeriodo: 1250953,
      deudaCuotasFacturar: 1610000,
      deudaTotal: 2860953,
      pagoMinimo: 398727,
      fechaVencimiento: '2026-07-08',
      tan: 17.83,
      tae: 19.51,
    });
  });
});
