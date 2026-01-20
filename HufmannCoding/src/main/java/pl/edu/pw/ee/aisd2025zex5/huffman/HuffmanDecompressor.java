package pl.edu.pw.ee.aisd2025zex5.huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import pl.edu.pw.ee.aisd2025zex5.exception.HuffmanException;
import pl.edu.pw.ee.aisd2025zex5.io.BitInputStream;

public class HuffmanDecompressor {

    public void decompress(String sourcePath, String destPath) {
        try {
            File sourceFile = new File(sourcePath);
            long fileSize = sourceFile.length();

            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(destPath)) {

                int L = fis.read();
                int bitPadding = fis.read();
                int bytePadding = fis.read();

                if (L == -1 || bitPadding == -1 || bytePadding == -1) {
                    throw new HuffmanException("Plik jest za krótki - brak nagłówka");
                }

                if (L < 1 || L > 255) {
                    throw new HuffmanException("Nieprawidłowa wartość L w nagłówku: " + L);
                }
                if (bitPadding < 0 || bitPadding > 7) {
                    throw new HuffmanException("Nieprawidłowa wartość bitPadding w nagłówku: " + bitPadding);
                }
                if (bytePadding < 0 || bytePadding >= L) {
                    throw new HuffmanException("Nieprawidłowa wartość bytePadding w nagłówku: " + bytePadding);
                }

                long remainingBytes = fileSize - 3;
                if (remainingBytes == 0) {
                    return; // tylko nagłówek => pusty wynik
                }

                long totalBits = remainingBytes * 8;
                if (bitPadding > totalBits) {
                    throw new HuffmanException("bitPadding większy niż liczba bitów w pliku");
                }
                long validBits = totalBits - bitPadding;

                try (BitInputStream bitIn = new BitInputStream(fis)) {
                    HuffmanNode root = readTree(bitIn, L);

                    long treeBits = bitIn.getTotalBitsRead();
                    if (treeBits > validBits) {
                        throw new HuffmanException("Uszkodzony plik: drzewo większe niż zawartość");
                    }

                    long dataBits = validBits - treeBits;
                    if (dataBits < 0) {
                        throw new HuffmanException("Uszkodzony plik: ujemna liczba bitów danych");
                    }

                    decodeData(bitIn, fos, root, dataBits, bytePadding);
                }
            }

        } catch (IOException e) {
            throw new HuffmanException("Błąd podczas dekompresji: " + e.getMessage(), e);
        }
    }

    private HuffmanNode readTree(BitInputStream bitIn, int L) throws IOException {
        int bit = bitIn.readBit();
        if (bit == -1) {
            throw new HuffmanException("Nieoczekiwany koniec pliku podczas czytania drzewa");
        }

        if (bit == 1) {
            byte[] symbolBytes = new byte[L];
            for (int i = 0; i < L; i++) {
                int b = bitIn.readByte(); // rzuci IOException jeśli EOF
                symbolBytes[i] = (byte) b;
            }
            String symbol = new String(symbolBytes, StandardCharsets.ISO_8859_1);
            return new HuffmanNode(symbol, 0);
        }

        HuffmanNode left = readTree(bitIn, L);
        HuffmanNode right = readTree(bitIn, L);
        return new HuffmanNode(left, right);
    }

    // Strumieniowo: nie trzymamy wszystkich symboli w RAM
    private void decodeData(BitInputStream bitIn, OutputStream out,
                            HuffmanNode root, long dataBits, int bytePadding) throws IOException {
        if (dataBits == 0) return;

        if (root.isLeaf()) {
            decodeSingleSymbol(bitIn, out, root, dataBits, bytePadding);
            return;
        }

        HuffmanNode current = root;
        long bitsRead = 0;
        String lastSymbol = null;

        while (bitsRead < dataBits) {
            int bit = bitIn.readBit();
            if (bit == -1) {
                throw new HuffmanException("Nieoczekiwany koniec danych (EOF) podczas dekodowania");
            }
            bitsRead++;

            current = (bit == 0) ? current.getLeft() : current.getRight();
            if (current == null) {
                throw new HuffmanException("Uszkodzone dane: ścieżka w drzewie prowadzi do null");
            }

            if (current.isLeaf()) {
                if (lastSymbol != null) {
                    out.write(lastSymbol.getBytes(StandardCharsets.ISO_8859_1));
                }
                lastSymbol = current.getSymbol();
                current = root;
            }
        }

        if (current != root) {
            throw new HuffmanException("Uszkodzone dane: zakończono w środku kodu Huffmana");
        }

        if (lastSymbol != null) {
            byte[] b = lastSymbol.getBytes(StandardCharsets.ISO_8859_1);
            int bytesToWrite = b.length - bytePadding;
            if (bytesToWrite < 0 || bytesToWrite > b.length) {
                throw new HuffmanException("Nieprawidłowy bytePadding: " + bytePadding);
            }
            if (bytesToWrite > 0) {
                out.write(b, 0, bytesToWrite);
            }
        }
    }

    private void decodeSingleSymbol(BitInputStream bitIn, OutputStream out,
                                    HuffmanNode root, long dataBits, int bytePadding) throws IOException {
        byte[] symbolBytes = root.getSymbol().getBytes(StandardCharsets.ISO_8859_1);

        long symbolCount = 0;
        for (long i = 0; i < dataBits; i++) {
            int bit = bitIn.readBit();
            if (bit == -1) {
                throw new HuffmanException("Nieoczekiwany koniec danych (EOF) podczas dekodowania");
            }
            if (bit != 0) {
                throw new HuffmanException("Uszkodzone dane: dla jednego symbolu oczekiwano samych bitów 0");
            }
            symbolCount++;
        }

        for (long i = 0; i < symbolCount; i++) {
            if (i == symbolCount - 1) {
                int bytesToWrite = symbolBytes.length - bytePadding;
                if (bytesToWrite < 0 || bytesToWrite > symbolBytes.length) {
                    throw new HuffmanException("Nieprawidłowy bytePadding: " + bytePadding);
                }
                if (bytesToWrite > 0) out.write(symbolBytes, 0, bytesToWrite);
            } else {
                out.write(symbolBytes);
            }
        }
    }
}
