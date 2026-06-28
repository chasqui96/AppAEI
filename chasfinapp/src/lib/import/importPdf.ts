import { guardarExtracto } from '../db/db';
import { readPdfLinesFromUri } from '../pdf/readPdfFile';
import { detectBank } from '../parsers/detectBank';
import { parseContinental } from '../parsers/continental';
import { parseItau } from '../parsers/itau';
import { parseUeno } from '../parsers/ueno';
import { ExtractoParseado } from '../parsers/types';

export class BancoNoDetectadoError extends Error {
  constructor() {
    super('No se pudo identificar el banco de este extracto.');
  }
}

export class ResumenIncompletoError extends Error {
  constructor() {
    super('No se pudo leer la fecha de vencimiento del extracto.');
  }
}

function parseByBanco(banco: ReturnType<typeof detectBank>, lines: string[]): ExtractoParseado {
  switch (banco) {
    case 'continental':
      return parseContinental(lines);
    case 'ueno':
      return parseUeno(lines);
    case 'itau':
      return parseItau(lines);
    default:
      throw new BancoNoDetectadoError();
  }
}

/** Imports a PDF statement picked from the device: extracts text, detects the bank,
 * parses it, and persists the result, grouping by the statement's vencimiento month. */
export async function importarExtractoPdf(uri: string): Promise<ExtractoParseado> {
  const lines = await readPdfLinesFromUri(uri);
  const banco = detectBank(lines);
  const extracto = parseByBanco(banco, lines);

  if (!extracto.resumen.fechaVencimiento) {
    throw new ResumenIncompletoError();
  }

  const periodo = extracto.resumen.fechaVencimiento.slice(0, 7);
  guardarExtracto(extracto, banco as string, periodo);

  return extracto;
}
