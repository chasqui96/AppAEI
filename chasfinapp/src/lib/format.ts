export function formatGs(value: number | null): string {
  if (value === null) return '-';
  const rounded = Math.round(value);
  return `Gs. ${rounded.toLocaleString('es-PY')}`;
}

export function formatPeriodo(periodo: string): string {
  const [year, month] = periodo.split('-');
  const meses = [
    'Ene',
    'Feb',
    'Mar',
    'Abr',
    'May',
    'Jun',
    'Jul',
    'Ago',
    'Sep',
    'Oct',
    'Nov',
    'Dic',
  ];
  const idx = parseInt(month, 10) - 1;
  return `${meses[idx] ?? month} ${year}`;
}
