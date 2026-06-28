export const CATEGORIA_OTROS = 'Otros';
export const CATEGORIA_PAGOS = 'Pagos';

/**
 * Ordered list of (categoria, keywords[]) — first matching keyword wins, so more
 * specific keywords should be listed before broader ones within the same category.
 */
export const REGLAS_DEFECTO: { categoria: string; keywords: string[] }[] = [
  {
    categoria: 'Supermercado',
    keywords: ['BIGGIE', 'SUPER', 'STOCK', 'CASA RICA', 'AREA UNO', 'SALEMMA'],
  },
  {
    categoria: 'Combustible',
    keywords: ['PETROPAR', 'PETROBRAS', 'SHELL', 'PUMA', 'ESTACION', 'EXPRESS'],
  },
  {
    categoria: 'Tecnología/Suscripción',
    keywords: [
      'CLAUDE.AI',
      'ANTHROPIC',
      'CANVA',
      'GOOGLE ONE',
      'GOOGLE',
      'SAMSUNG',
      'SONY ONE',
      'MICROSOFT',
      'APPLE',
      'ICLOUD',
    ],
  },
  {
    categoria: 'Entretenimiento/Suscripción',
    keywords: ['SPOTIFY', 'NETFLIX', 'DISNEY', 'HBO', 'YOUTUBE', 'AMAZON PRIME'],
  },
  {
    categoria: 'Impuestos/Municipales',
    keywords: ['MUNICIPALIDAD', 'SET ', 'IMPUESTO'],
  },
  {
    categoria: 'Intereses/Cargos',
    keywords: [
      'SEG.DE CANC',
      'SEGURO',
      'MANTENIM',
      'CARGO POR COMPRAS INTERNACIONALES',
      'IVA LEY',
      'GASTOS FINANCIEROS',
      'INTERES',
    ],
  },
  {
    categoria: 'Joyería/Regalos',
    keywords: ['NEUSA JOYAS', 'JOYERIA'],
  },
  {
    categoria: 'Deporte/Salud',
    keywords: ['SPORTFINO', 'FARMACIA', 'GYM', 'GIMNASIO'],
  },
  {
    categoria: 'Restaurantes',
    keywords: ['RESTAURANT', 'PARRILLA', 'PIZZA', 'BURGER', 'MC DONALD', 'CAFE'],
  },
  {
    categoria: 'Transporte',
    keywords: ['UBER', 'BOLT', 'TAXI'],
  },
  {
    categoria: 'Servicios/Pagos',
    keywords: ['WEPA', 'TIGO', 'CLARO', 'PERSONAL', 'ANDE', 'ESSAP', 'COPACO'],
  },
];

export const TODAS_LAS_CATEGORIAS = [
  ...REGLAS_DEFECTO.map((r) => r.categoria),
  CATEGORIA_OTROS,
  CATEGORIA_PAGOS,
];
