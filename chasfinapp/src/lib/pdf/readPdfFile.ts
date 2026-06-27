import * as FileSystem from 'expo-file-system';
import { toByteArray } from 'base64-js';

import { extractPdfLines } from './extractPdfLines';

/** Reads a PDF picked via expo-document-picker and returns its reconstructed text lines. */
export async function readPdfLinesFromUri(uri: string): Promise<string[]> {
  const base64 = await FileSystem.readAsStringAsync(uri, {
    encoding: FileSystem.EncodingType.Base64,
  });
  const bytes = toByteArray(base64);
  return extractPdfLines(bytes);
}
