import * as pdfjsLib from 'pdfjs-dist/legacy/build/pdf';

/**
 * Extracts text from a PDF as an array of reconstructed lines, ordered top to bottom,
 * with tokens on the same visual row joined left-to-right. Runs in pure JS (no canvas,
 * no Worker) so it works directly on Hermes without any native module or backend.
 */
export async function extractPdfLines(data: Uint8Array): Promise<string[]> {
  const doc = await pdfjsLib.getDocument({ data, disableWorker: true } as any).promise;
  const lines: string[] = [];

  for (let pageNum = 1; pageNum <= doc.numPages; pageNum++) {
    const page = await doc.getPage(pageNum);
    const content = await page.getTextContent();

    const items = content.items
      .filter((it: any) => typeof it.str === 'string')
      .map((it: any) => ({
        str: it.str as string,
        x: it.transform[4] as number,
        y: it.transform[5] as number,
      }));

    const rows = new Map<number, { str: string; x: number }[]>();
    for (const item of items) {
      const key = Math.round(item.y);
      const row = rows.get(key) ?? [];
      row.push(item);
      rows.set(key, row);
    }

    const sortedYs = [...rows.keys()].sort((a, b) => b - a);
    for (const y of sortedYs) {
      const rowItems = rows.get(y)!.sort((a, b) => a.x - b.x);
      const line = rowItems
        .map((it) => it.str)
        .join(' ')
        .replace(/\s+/g, ' ')
        .trim();
      if (line) lines.push(line);
    }
  }

  return lines;
}
