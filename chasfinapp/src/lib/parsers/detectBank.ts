import { Banco } from './types';

export function detectBank(lines: string[]): Banco | null {
  const text = lines.join(' ').toLowerCase();

  if (text.includes('ueno.com.py') || text.includes('ueno bank')) return 'ueno';
  if (text.includes('itau.com.py') || text.includes('itaú') || text.includes('itau')) return 'itau';
  if (text.includes('continental') || text.includes('bancontinental')) return 'continental';

  return null;
}
