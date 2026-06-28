export function toIsoDate(ddmmyy: string): string {
  const [d, m, y] = ddmmyy.split('/');
  const year = y.length === 2 ? `20${y}` : y;
  return `${year}-${m.padStart(2, '0')}-${d.padStart(2, '0')}`;
}

/** Parses amounts formatted with "." as thousands separator and "," as decimal (Continental, Itaú). */
export function parseAmountDot(raw: string): number {
  const negative = raw.trim().startsWith('-');
  const cleaned = raw.replace(/[^\d,.\-]/g, '');
  const normalized = cleaned.includes(',')
    ? cleaned.replace(/\./g, '').replace(',', '.')
    : cleaned.replace(/\./g, '');
  const value = Math.abs(parseFloat(normalized));
  return negative ? -value : value;
}

export function lastNumberTokenDot(line: string): { value: number; index: number } | null {
  const match = line.match(/(-?\d{1,3}(?:\.\d{3})*)\s*$/);
  if (!match) return null;
  return { value: parseAmountDot(match[1]), index: match.index ?? line.length };
}

/** Parses amounts formatted with "," as thousands separator and "." as decimal (Ueno). */
export function parseAmountComma(raw: string): number {
  const negative = raw.trim().startsWith('-');
  const cleaned = raw.replace(/[^\d,.\-]/g, '').replace(/,/g, '');
  const value = Math.abs(parseFloat(cleaned));
  return negative ? -value : value;
}

export function lastNumberTokenComma(line: string): { value: number; index: number } | null {
  const match = line.match(/(-?\d{1,3}(?:,\d{3})*)\s*$/);
  if (!match) return null;
  return { value: parseAmountComma(match[1]), index: match.index ?? line.length };
}

/** Parses Paraguayan-style decimal rates like "17,85" -> 17.85 (comma decimal, used for TAN/TAE everywhere). */
export function parseRateComma(raw: string): number {
  return parseFloat(raw.replace(',', '.'));
}

/**
 * Finds a labeled summary value where the number may sit on the same line as the
 * label (Ueno, Itaú) or on the very next line (Continental's box layout).
 */
export function findResumenValue(
  lines: string[],
  labelTest: (line: string) => boolean,
  lastNumberFn: (line: string) => { value: number; index: number } | null
): number | null {
  const idx = lines.findIndex(labelTest);
  if (idx === -1) return null;

  const sameLine = lastNumberFn(lines[idx]);
  if (sameLine) return sameLine.value;

  if (idx + 1 < lines.length) {
    const nextLine = lastNumberFn(lines[idx + 1]);
    if (nextLine) return nextLine.value;
  }
  return null;
}

export function findRateOnLabelLine(lines: string[], regex: RegExp): number | null {
  for (const line of lines) {
    const match = line.match(regex);
    if (match) return parseRateComma(match[1]);
  }
  return null;
}

/** Like findRateOnLabelLine, but also checks the following line for the rate value
 * (some bank layouts wrap the label and value onto separate visual rows). */
export function findRateNearLabel(lines: string[], labelRegex: RegExp, rateRegex = /(\d+,\d+)/): number | null {
  for (let i = 0; i < lines.length; i++) {
    if (!labelRegex.test(lines[i])) continue;
    const sameLine = lines[i].match(rateRegex);
    if (sameLine) return parseRateComma(sameLine[1]);
    if (i + 1 < lines.length) {
      const nextLine = lines[i + 1].match(rateRegex);
      if (nextLine) return parseRateComma(nextLine[1]);
    }
  }
  return null;
}
