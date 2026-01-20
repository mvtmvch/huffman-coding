package pl.edu.pw.ee.aisd2025zex5.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Strumień do zapisu pojedynczych bitów.
 * 
 * Problem: OutputStream zapisuje tylko całe BAJTY (8 bitów).
 * Rozwiązanie: Buforujemy bity i zapisujemy gdy uzbieramy 8.
 * 
 * Przykład zapisu bitów: 1, 0, 1, 1, 0, 0, 1, 0, 1, 1
 * 
 *   Bit │ Bufor (binarnie) │ Akcja
 *   ────┼──────────────────┼─────────────────
 *    1  │ 00000001         │ bufferLength = 1
 *    0  │ 00000010         │ bufferLength = 2
 *    1  │ 00000101         │ bufferLength = 3
 *    1  │ 00001011         │ bufferLength = 4
 *    0  │ 00010110         │ bufferLength = 5
 *    0  │ 00101100         │ bufferLength = 6
 *    1  │ 01011001         │ bufferLength = 7
 *    0  │ 10110010         │ bufferLength = 8 → ZAPISZ! (178)
 *   ────┼──────────────────┼─────────────────
 *    1  │ 00000001         │ nowy bufor
 *    1  │ 00000011         │ bufferLength = 2
 *       │                  │ close() → dopełnij zerami → 11000000 (192)
 */
public class BitOutputStream implements AutoCloseable {

    private final OutputStream outputStream;
    
    private int buffer;        // Bufor na bity (max 8 bitów)
    private int bufferLength;  // Ile bitów jest w buforze (0-7)
    private int paddingAdded = 0;  // Ile zer dopełniających dodano przy close()

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.buffer = 0;
        this.bufferLength = 0;
        this.paddingAdded = 0;
    }

    /**
     * Zapisuje pojedynczy bit (0 lub 1).
     * 
     * Operacja: buffer = (buffer << 1) | bit
     * 
     * Przykład:
     *   buffer = 00000101 (5), bit = 1
     *   buffer << 1 = 00001010 (10)
     *   10 | 1 = 00001011 (11)
     */
    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Bit musi być 0 lub 1, otrzymano: " + bit);
        }

        // Przesuń bufor w lewo i dodaj nowy bit na końcu
        buffer = (buffer << 1) | bit;
        bufferLength++;

        // Jeśli bufor pełny (8 bitów), zapisz bajt
        if (bufferLength == 8) {
            outputStream.write(buffer);
            buffer = 0;
            bufferLength = 0;
        }
    }

    /**
     * Zapisuje bajt jako 8 bitów (od najstarszego do najmłodszego).
     * 
     * Przykład dla bajtu 65 (01000001, czyli 'A'):
     *   i=7: (65 >> 7) & 1 = 0  → writeBit(0)
     *   i=6: (65 >> 6) & 1 = 1  → writeBit(1)
     *   i=5: (65 >> 5) & 1 = 0  → writeBit(0)
     *   ...
     *   i=0: (65 >> 0) & 1 = 1  → writeBit(1)
     */
    public void writeByte(int b) throws IOException {
        for (int i = 7; i >= 0; i--) {
            int bit = (b >> i) & 1;
            writeBit(bit);
        }
    }

    /**
     * Zamyka strumień.
     * 
     * Jeśli w buforze zostały bity, dopełnia zerami do 8 i zapisuje.
     * 
     * Przykład:
     *   bufor = 00000011 (2 bity: "11")
     *   dopełnienie: 11000000 (zapisz 192)
     */
    @Override
    public void close() throws IOException {
        // Dopełnij bufor zerami jeśli niepełny
        if (bufferLength > 0) {
            paddingAdded = 8 - bufferLength;  // Zapisz ile zer dodano
            buffer = buffer << paddingAdded;  // Przesuń bity na początek bajtu
            outputStream.write(buffer);
        }
        outputStream.close();
    }
    
    /**
     * Zwraca ile bitów dopełniających (zer) dodano przy close().
     * Wywołuj PO close()!
     */
    public int getPaddingAdded() {
        return paddingAdded;
    }
}
