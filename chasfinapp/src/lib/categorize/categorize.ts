import { CATEGORIA_OTROS, CATEGORIA_PAGOS, REGLAS_DEFECTO } from './categorias';

/** Normalizes a merchant string into the key used to store/look up learned rules. */
export function normalizeComercioKey(comercio: string): string {
  return comercio.trim().toUpperCase();
}

/**
 * Resolves the category for a transaction. Learned rules (from manual
 * re-categorization) take priority over the default keyword dictionary, since
 * they represent an explicit user correction for that merchant.
 */
export function categorize(
  comercio: string,
  esPago: boolean,
  learnedRules: Record<string, string> = {}
): string {
  if (esPago) return CATEGORIA_PAGOS;

  const upper = comercio.toUpperCase();

  for (const [keyword, categoria] of Object.entries(learnedRules)) {
    if (upper.includes(keyword)) return categoria;
  }

  for (const { categoria, keywords } of REGLAS_DEFECTO) {
    if (keywords.some((keyword) => upper.includes(keyword))) return categoria;
  }

  return CATEGORIA_OTROS;
}
