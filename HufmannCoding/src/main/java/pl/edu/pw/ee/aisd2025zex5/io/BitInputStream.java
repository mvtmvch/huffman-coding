package pl.edu.pw.ee.aisd2025zex5.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Strumień do odczytu pojedynczych bitów.
 * 
 * Problem: InputStream czyta tylko całe BAJTY.
 * Rozwiązanie: Czytamy bajt i udostępniamy bit po bicie.
 * 
 * Przykład odczytu bajtu 178 (10110010):
 * 
 *   Wywołanie │ bitsRemaining │ Zwracany bit
 *   ──────────┼───────────────┼─────────────
 *   readBit() │ 8 → 7         │ 1 (bit 7)
 *   readBit() │ 7 → 6         │ 0 (bit 6)
 *   readBit() │ 6 → 5         │ 1 (bit 5)
 *   readBit() │ 5 → 4         │ 1 (bit 4)
 *   readBit() │ 4 → 3         │ 0 (bit 3)
 *   readBit() │ 3 → 2         │ 0 (bit 2)
 *   readBit() │ 2 → 1         │ 1 (bit 1)
 *   readBit() │ 1 → 0         │ 0 (bit 0)
 *   readBit() │ 0 → czytaj nowy bajt...
 */
public class BitInputStream implements AutoCloseable {

    private final InputStream inputStream;
    
    private int currentByte;    // Aktualnie przetwarzany bajt
    private int bitsRemaining;  // Ile bitów zostało do odczytania z currentByte
    private long totalBitsRead; // Licznik odczytanych bitów (do kontroli paddingu)

    public BitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.currentByte = 0;
        this.bitsRemaining = 0;
        this.totalBitsRead = 0;
    }

    /**
     * Czyta pojedynczy bit.
     * 
     * Operacja: (currentByte >> bitsRemaining) & 1
     * 
     * Przykład dla bajtu 178 (10110010), bitsRemaining = 5:
     *   178 >> 5 = 00000101
     *   00000101 & 1 = 1 (bit na pozycji 5)
     * 
     * @return 0 lub 1, albo -1 jeśli koniec strumienia
     */
    public int readBit() throws IOException {
        // Jeśli bufor pusty, czytaj nowy bajt
        if (bitsRemaining == 0) {
            currentByte = inputStream.read();
            if (currentByte == -1) {
                return -1;  // Koniec strumienia
            }
            bitsRemaining = 8;
        }

        bitsRemaining--;
        totalBitsRead++;

        // Wyciągnij bit z pozycji bitsRemaining
        return (currentByte >> bitsRemaining) & 1;
    }

    /**
     * Czyta 8 bitów i składa w bajt.
     * 
     * Przykład:
     *   readBit() → 0  →  result = 0
     *   readBit() → 1  →  result = (0 << 1) | 1 = 1
     *   readBit() → 0  →  result = (1 << 1) | 0 = 2
     *   readBit() → 0  →  result = (2 << 1) | 0 = 4
     *   readBit() → 0  →  result = (4 << 1) | 0 = 8
     *   readBit() → 0  →  result = (8 << 1) | 0 = 16
     *   readBit() → 0  →  result = (16 << 1) | 0 = 32
     *   readBit() → 1  →  result = (32 << 1) | 1 = 65 ('A')
     */
    public int readByte() throws IOException {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit();
            if (bit == -1) {
                throw new IOException("Nieoczekiwany koniec strumienia podczas czytania bajtu");
            }
            result = (result << 1) | bit;
        }
        return result;
    }

    /**
     * Zwraca liczbę dotychczas odczytanych bitów.
     * 
     * Używane do kontroli: ile bitów to dane, a ile to padding.
     */
    public long getTotalBitsRead() {
        return totalBitsRead;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
