import fs from 'fs';
import path from 'path';

import { extractPdfLines } from '../../pdf/extractPdfLines';
import { parseContinental } from '../continental';

describe('parseContinental', () => {
  it('parses transactions and the financial summary from a real Continental extracto', async () => {
    const pdfPath = path.join(__dirname, '..', '__fixtures__', 'continental-sample.pdf');
    const data = new Uint8Array(fs.readFileSync(pdfPath));
    const lines = await extractPdfLines(data);

    const { banco, transacciones, resumen } = parseContinental(lines);

    expect(banco).toBe('continental');
    expect(transacciones.length).toBeGreaterThanOrEqual(20);

    const contimarket = transacciones.find((t) => t.comercio.includes('CONTIMARKET CHECKOUT CTA.003/003'));
    expect(contimarket).toEqual({
      fecha: '2026-05-25',
      comercio: 'CONTIMARKET CHECKOUT CTA.003/003',
      monto: 252666,
      esPago: false,
    });

    const pago = transacciones.find((t) => t.esPago && t.monto === 164000);
    expect(pago).toBeDefined();
    expect(pago?.comercio).toBe('SU PAGO GRACIAS');

    const claudeAi = transacciones.find((t) => t.comercio.includes('CLAUDE.AI'));
    expect(claudeAi?.monto).toBe(124100);

    const anthropic = transacciones.find((t) => t.comercio.includes('ANTHROPIC'));
    expect(anthropic?.monto).toBe(31365);

    expect(resumen).toEqual({
      deudaAnterior: 257160,
      pagos: 331160,
      saldoFinanciado: -74000,
      comprasDelMes: 1221659,
      deudaTotalPeriodo: 1147659,
      deudaCuotasFacturar: 1695950,
      deudaTotal: 2843609,
      pagoMinimo: 727000,
      fechaVencimiento: '2026-07-10',
      tan: 17.85,
      tae: 19.54,
    });
  });
});
