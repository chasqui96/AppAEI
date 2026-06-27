export type Banco = 'ueno' | 'itau' | 'continental';

export interface Transaccion {
  fecha: string; // YYYY-MM-DD
  comercio: string;
  monto: number;
  esPago: boolean;
}

export interface ResumenFinanciero {
  deudaAnterior: number | null;
  pagos: number | null;
  saldoFinanciado: number | null;
  comprasDelMes: number | null;
  deudaTotalPeriodo: number | null;
  deudaCuotasFacturar: number | null;
  deudaTotal: number | null;
  pagoMinimo: number | null;
  fechaVencimiento: string | null; // YYYY-MM-DD
  tan: number | null;
  tae: number | null;
}

export interface ExtractoParseado {
  banco: Banco;
  transacciones: Transaccion[];
  resumen: ResumenFinanciero;
}
