import fs from 'fs';
import path from 'path';

import { extractPdfLines } from '../../pdf/extractPdfLines';
import { detectBank } from '../detectBank';

describe('detectBank', () => {
  it.each([
    ['continental-sample.pdf', 'continental'],
    ['ueno-sample.pdf', 'ueno'],
    ['itau-sample.pdf', 'itau'],
  ])('detects %s as %s', async (fixture, expected) => {
    const pdfPath = path.join(__dirname, '..', '__fixtures__', fixture);
    const data = new Uint8Array(fs.readFileSync(pdfPath));
    const lines = await extractPdfLines(data);
    expect(detectBank(lines)).toBe(expected);
  });
});
