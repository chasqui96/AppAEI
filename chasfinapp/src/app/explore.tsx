import { useCallback, useEffect, useState } from 'react';
import { FlatList, Pressable, StyleSheet, TextInput, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Accent, Colors, Spacing } from '@/constants/theme';
import { TODAS_LAS_CATEGORIAS } from '@/lib/categorize/categorias';
import { TransaccionRow, getTransacciones, setCategoriaTransaccion } from '@/lib/db/db';
import { formatGs } from '@/lib/format';

export default function MovimientosScreen() {
  const [busqueda, setBusqueda] = useState('');
  const [transacciones, setTransacciones] = useState<TransaccionRow[]>([]);
  const [expandidoId, setExpandidoId] = useState<number | null>(null);

  const recargar = useCallback(() => {
    setTransacciones(getTransacciones(undefined, busqueda.trim() || undefined));
  }, [busqueda]);

  useEffect(() => {
    recargar();
  }, [recargar]);

  const handleRecategorizar = useCallback(
    (tx: TransaccionRow, categoria: string) => {
      setCategoriaTransaccion(tx.id, tx.comercio, categoria);
      setExpandidoId(null);
      recargar();
    },
    [recargar]
  );

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <View style={styles.header}>
          <ThemedText type="title" style={styles.headerTitle}>
            Movimientos
          </ThemedText>
          <TextInput
            value={busqueda}
            onChangeText={setBusqueda}
            placeholder="Buscar comercio…"
            placeholderTextColor={Colors.dark.textSecondary}
            style={styles.searchInput}
          />
        </View>

        <FlatList
          data={transacciones}
          keyExtractor={(item) => String(item.id)}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.emptyState}>
              <ThemedText themeColor="textSecondary">No hay movimientos para mostrar.</ThemedText>
            </ThemedView>
          }
          renderItem={({ item }) => {
            const expandido = expandidoId === item.id;
            return (
              <Pressable onPress={() => setExpandidoId(expandido ? null : item.id)}>
                <ThemedView type="backgroundElement" style={styles.row}>
                  <View style={styles.rowMain}>
                    <View style={styles.rowInfo}>
                      <ThemedText type="smallBold">{item.comercio}</ThemedText>
                      <ThemedText type="small" themeColor="textSecondary">
                        {item.fecha} · {item.banco.toUpperCase()} · {item.categoria}
                      </ThemedText>
                    </View>
                    <ThemedText
                      type="smallBold"
                      style={item.esPago ? styles.montoPago : styles.montoCompra}>
                      {formatGs(item.monto)}
                    </ThemedText>
                  </View>

                  {expandido && (
                    <View style={styles.categoriaPicker}>
                      {TODAS_LAS_CATEGORIAS.map((cat) => (
                        <Pressable
                          key={cat}
                          onPress={() => handleRecategorizar(item, cat)}
                          style={[styles.chip, cat === item.categoria && styles.chipSelected]}>
                          <ThemedText style={styles.chipText}>{cat}</ThemedText>
                        </Pressable>
                      ))}
                    </View>
                  )}
                </ThemedView>
              </Pressable>
            );
          }}
        />
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
  header: {
    paddingHorizontal: Spacing.three,
    paddingTop: Spacing.three,
    gap: Spacing.two,
  },
  headerTitle: {
    fontSize: 24,
    lineHeight: 28,
  },
  searchInput: {
    backgroundColor: Colors.dark.backgroundElement,
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    color: Colors.dark.text,
    borderWidth: 1,
    borderColor: Accent.border,
  },
  listContent: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
    gap: Spacing.two,
  },
  emptyState: {
    padding: Spacing.four,
    borderRadius: Spacing.three,
  },
  row: {
    borderRadius: Spacing.three,
    padding: Spacing.three,
    marginBottom: Spacing.two,
  },
  rowMain: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  rowInfo: {
    flex: 1,
    gap: 2,
  },
  montoCompra: {
    color: Accent.terracottaLight,
  },
  montoPago: {
    color: Accent.greenLight,
  },
  categoriaPicker: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.one,
    marginTop: Spacing.three,
  },
  chip: {
    paddingHorizontal: Spacing.two,
    paddingVertical: 4,
    borderRadius: Spacing.five,
    backgroundColor: Colors.dark.backgroundSelected,
    borderWidth: 1,
    borderColor: Accent.border,
  },
  chipSelected: {
    backgroundColor: Accent.greenDark,
    borderColor: Accent.greenLight,
  },
  chipText: {
    fontSize: 11,
  },
});
