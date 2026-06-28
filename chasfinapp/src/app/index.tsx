import { useCallback, useEffect, useState } from 'react';
import { Alert, Pressable, ScrollView, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import * as DocumentPicker from 'expo-document-picker';
import { LineChart, PieChart } from 'react-native-gifted-charts';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Accent, CategoriaColors, Colors, Spacing } from '@/constants/theme';
import {
  CategoriaTotal,
  TarjetaConResumen,
  TendenciaMensual,
  getPeriodosDisponibles,
  getResumenesPorPeriodo,
  getTendenciaMensual,
  getTotalesPorCategoria,
} from '@/lib/db/db';
import { formatGs, formatPeriodo } from '@/lib/format';
import { importarExtractoPdf } from '@/lib/import/importPdf';

export default function DashboardScreen() {
  const [periodos, setPeriodos] = useState<string[]>([]);
  const [periodoSeleccionado, setPeriodoSeleccionado] = useState<string | null>(null);
  const [tarjetas, setTarjetas] = useState<TarjetaConResumen[]>([]);
  const [categorias, setCategorias] = useState<CategoriaTotal[]>([]);
  const [tendencia, setTendencia] = useState<TendenciaMensual[]>([]);
  const [importando, setImportando] = useState(false);

  const recargarPeriodos = useCallback(() => {
    const disponibles = getPeriodosDisponibles();
    setPeriodos(disponibles);
    setPeriodoSeleccionado((actual) => actual ?? disponibles[0] ?? null);
    setTendencia(getTendenciaMensual());
    return disponibles;
  }, []);

  useEffect(() => {
    recargarPeriodos();
  }, [recargarPeriodos]);

  useEffect(() => {
    if (!periodoSeleccionado) {
      setTarjetas([]);
      setCategorias([]);
      return;
    }
    setTarjetas(getResumenesPorPeriodo(periodoSeleccionado));
    setCategorias(getTotalesPorCategoria(periodoSeleccionado));
  }, [periodoSeleccionado]);

  const handleImportar = useCallback(async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({ type: 'application/pdf' });
      if (result.canceled || !result.assets[0]) return;

      setImportando(true);
      const extracto = await importarExtractoPdf(result.assets[0].uri);
      const disponibles = recargarPeriodos();
      const periodo = extracto.resumen.fechaVencimiento?.slice(0, 7) ?? disponibles[0];
      setPeriodoSeleccionado(periodo);
    } catch (error) {
      Alert.alert('No se pudo importar', error instanceof Error ? error.message : String(error));
    } finally {
      setImportando(false);
    }
  }, [recargarPeriodos]);

  const totalCombinado = tarjetas.reduce((sum, t) => sum + (t.deudaTotal ?? 0), 0);

  const pieData = categorias.map((c, i) => ({
    value: Math.abs(c.total),
    color: CategoriaColors[i % CategoriaColors.length],
    text: c.categoria,
  }));

  const lineData = tendencia.map((t) => ({
    value: Math.abs(t.total),
    label: formatPeriodo(t.periodo),
  }));

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.header}>
            <ThemedText type="title" style={styles.headerTitle}>
              ChasFinApp
            </ThemedText>
            <Pressable
              onPress={handleImportar}
              disabled={importando}
              style={({ pressed }) => [styles.importButton, pressed && styles.pressed]}>
              <ThemedText style={styles.importButtonText}>
                {importando ? 'Importando…' : '+ Importar PDF'}
              </ThemedText>
            </Pressable>
          </View>

          {periodos.length === 0 && (
            <ThemedView type="backgroundElement" style={styles.emptyState}>
              <ThemedText themeColor="textSecondary">
                Todavía no importaste ningún extracto. Tocá &quot;+ Importar PDF&quot; para empezar.
              </ThemedText>
            </ThemedView>
          )}

          {periodos.length > 0 && (
            <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.pillRow}>
              {periodos.map((p) => (
                <Pressable
                  key={p}
                  onPress={() => setPeriodoSeleccionado(p)}
                  style={[styles.pill, p === periodoSeleccionado && styles.pillSelected]}>
                  <ThemedText
                    style={styles.pillText}
                    themeColor={p === periodoSeleccionado ? 'text' : 'textSecondary'}>
                    {formatPeriodo(p)}
                  </ThemedText>
                </Pressable>
              ))}
            </ScrollView>
          )}

          {periodoSeleccionado && (
            <ThemedView type="backgroundElement" style={styles.totalBanner}>
              <ThemedText themeColor="textSecondary" type="small">
                Deuda total combinada
              </ThemedText>
              <ThemedText type="subtitle" style={styles.totalAmount}>
                {formatGs(totalCombinado)}
              </ThemedText>
            </ThemedView>
          )}

          {tarjetas.map((t) => (
            <ThemedView key={t.resumenId} type="backgroundElement" style={styles.cardSummary}>
              <View style={styles.cardSummaryHeader}>
                <ThemedText type="smallBold" style={styles.cardBanco}>
                  {t.banco.toUpperCase()}
                </ThemedText>
                <ThemedText themeColor="textSecondary" type="small">
                  Vence {t.fechaVencimiento ?? '-'}
                </ThemedText>
              </View>
              <View style={styles.cardSummaryRow}>
                <ThemedText themeColor="textSecondary" type="small">
                  Compras del mes
                </ThemedText>
                <ThemedText type="smallBold">{formatGs(t.comprasDelMes)}</ThemedText>
              </View>
              <View style={styles.cardSummaryRow}>
                <ThemedText themeColor="textSecondary" type="small">
                  Deuda total
                </ThemedText>
                <ThemedText type="smallBold">{formatGs(t.deudaTotal)}</ThemedText>
              </View>
              <View style={styles.cardSummaryRow}>
                <ThemedText themeColor="textSecondary" type="small">
                  Pago mínimo
                </ThemedText>
                <ThemedText type="smallBold">{formatGs(t.pagoMinimo)}</ThemedText>
              </View>
            </ThemedView>
          ))}

          {pieData.length > 0 && (
            <ThemedView type="backgroundElement" style={styles.chartCard}>
              <ThemedText type="smallBold" style={styles.chartTitle}>
                Gastos por categoría
              </ThemedText>
              <View style={styles.donutWrapper}>
                <PieChart
                  data={pieData}
                  donut
                  radius={90}
                  innerRadius={60}
                  innerCircleColor={Colors.dark.backgroundElement}
                  centerLabelComponent={() => (
                    <ThemedText type="small" themeColor="textSecondary">
                      {formatGs(categorias.reduce((s, c) => s + c.total, 0))}
                    </ThemedText>
                  )}
                />
              </View>
              <View style={styles.legend}>
                {categorias.map((c, i) => (
                  <View key={c.categoria} style={styles.legendRow}>
                    <View
                      style={[styles.legendDot, { backgroundColor: CategoriaColors[i % CategoriaColors.length] }]}
                    />
                    <ThemedText style={styles.legendLabel} type="small">
                      {c.categoria}
                    </ThemedText>
                    <ThemedText type="small" themeColor="textSecondary">
                      {formatGs(c.total)}
                    </ThemedText>
                  </View>
                ))}
              </View>
            </ThemedView>
          )}

          {lineData.length > 1 && (
            <ThemedView type="backgroundElement" style={styles.chartCard}>
              <ThemedText type="smallBold" style={styles.chartTitle}>
                Tendencia mensual
              </ThemedText>
              <LineChart
                data={lineData}
                color={Accent.greenLight}
                thickness={2}
                dataPointsColor={Accent.gold}
                yAxisTextStyle={{ color: Colors.dark.textSecondary }}
                xAxisLabelTextStyle={{ color: Colors.dark.textSecondary, fontSize: 10 }}
                hideRules
                yAxisColor={Colors.dark.backgroundSelected}
                xAxisColor={Colors.dark.backgroundSelected}
              />
            </ThemedView>
          )}
        </ScrollView>
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  safeArea: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: Spacing.three,
    paddingBottom: Spacing.six,
    gap: Spacing.three,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: Spacing.three,
  },
  headerTitle: {
    fontSize: 24,
    lineHeight: 28,
  },
  importButton: {
    backgroundColor: Accent.greenDark,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Spacing.five,
  },
  importButtonText: {
    fontSize: 13,
    fontWeight: '600',
  },
  pressed: {
    opacity: 0.7,
  },
  emptyState: {
    padding: Spacing.four,
    borderRadius: Spacing.three,
  },
  pillRow: {
    flexGrow: 0,
  },
  pill: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Spacing.five,
    marginRight: Spacing.two,
    backgroundColor: Colors.dark.backgroundElement,
    borderWidth: 1,
    borderColor: Accent.border,
  },
  pillSelected: {
    backgroundColor: Accent.greenDark,
    borderColor: Accent.greenLight,
  },
  pillText: {
    fontSize: 13,
    fontWeight: '600',
  },
  totalBanner: {
    padding: Spacing.four,
    borderRadius: Spacing.three,
    gap: Spacing.one,
  },
  totalAmount: {
    color: Accent.gold,
  },
  cardSummary: {
    padding: Spacing.three,
    borderRadius: Spacing.three,
    gap: Spacing.two,
  },
  cardSummaryHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: Spacing.one,
  },
  cardBanco: {
    color: Accent.terracottaLight,
  },
  cardSummaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  chartCard: {
    padding: Spacing.three,
    borderRadius: Spacing.three,
    gap: Spacing.three,
  },
  chartTitle: {
    marginBottom: Spacing.one,
  },
  donutWrapper: {
    alignItems: 'center',
  },
  legend: {
    gap: Spacing.two,
  },
  legendRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  legendDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
  },
  legendLabel: {
    flex: 1,
  },
});
