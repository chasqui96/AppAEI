import { categorize, normalizeComercioKey } from '../categorize';

describe('categorize', () => {
  it('marks pagos regardless of merchant text', () => {
    expect(categorize('SU PAGO, GRACIAS.', true)).toBe('Pagos');
  });

  it('matches known merchants to their category', () => {
    expect(categorize('NEUSA JOYAS 05/10', false)).toBe('Joyería/Regalos');
    expect(categorize('PETROPAR 10 DE AGOSTO', false)).toBe('Combustible');
    expect(categorize('SAMSUNG ELECTRONIC', false)).toBe('Tecnología/Suscripción');
    expect(categorize('SPOTIFY', false)).toBe('Entretenimiento/Suscripción');
    expect(categorize('Seg.de canc.Deuda', false)).toBe('Intereses/Cargos');
  });

  it('falls back to Otros for unknown merchants', () => {
    expect(categorize('UNKNOWN MERCHANT XYZ', false)).toBe('Otros');
  });

  it('prioritizes learned rules over the default dictionary', () => {
    const learned = { 'UNKNOWN MERCHANT': 'Mascotas' };
    expect(categorize('UNKNOWN MERCHANT XYZ', false, learned)).toBe('Mascotas');
  });

  it('normalizes merchant keys for storage', () => {
    expect(normalizeComercioKey('  Neusa Joyas 05/10  ')).toBe('NEUSA JOYAS 05/10');
  });
});
