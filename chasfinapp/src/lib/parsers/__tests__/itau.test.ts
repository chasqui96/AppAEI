import fs from 'fs';
import path from 'path';

import { extractPdfLines } from '../../pdf/extractPdfLines';
import { parseItau } from '../itau';

describe('parseItau', () => {
  it('parses transactions and the financial summary from a real Itaú extracto', async () => {
    const pdfPath = path.join(__dirname, '..', '__fixtures__', 'itau-sample.pdf');
    const data = new Uint8Array(fs.readFileSync(pdfPath));
    const lines = await extractPdfLines(data);

    const { banco, transacciones, resumen } = parseItau(lines);

    expect(banco).toBe('itau');

    const pago = transacciones.find((t) => t.esPago);
    expect(pago).toEqual({
      fecha: '2026-05-29',
      comercio: 'SU PAGO, GRACIAS.',
      monto: -1161384,
      esPago: true,
    });

    const spotify = transacciones.find((t) => t.comercio.includes('SPOTIFY'));
    expect(spotify?.monto).toBe(61938);
    expect(spotify?.esPago).toBe(false);

    const tiendaNaranja1 = transacciones.find((t) => t.comercio.includes('TIENDA NARANJA') && t.monto === 234000);
    expect(tiendaNaranja1).toBeTruthy();

    const tiendaNaranja2 = transacciones.find((t) => t.comercio.includes('TIENDA NARANJA') && t.monto === 262800);
    expect(tiendaNaranja2).toBeTruthy();

    const seguro = transacciones.find((t) => t.comercio.includes('Seg.de canc.Deuda'));
    expect(seguro?.monto).toBe(11095);
    expect(seguro?.esPago).toBe(false);

    expect(resumen).toEqual({
      deudaAnterior: 1161384,
      pagos: -1161384,
      saldoFinanciado: 0,
      comprasDelMes: 570942,
      deudaTotalPeriodo: 570942,
      deudaCuotasFacturar: 2570400,
      deudaTotal: 3141342,
      pagoMinimo: 511000,
      fechaVencimiento: '2026-07-02',
      tan: 18,
      tae: 0,
    });
  });
});
