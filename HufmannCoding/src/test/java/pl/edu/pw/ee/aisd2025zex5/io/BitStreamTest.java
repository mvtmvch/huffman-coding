package pl.edu.pw.ee.aisd2025zex5.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testy dla BitInputStream i BitOutputStream.
 */
class BitStreamTest {

    private ByteArrayOutputStream byteOut;
    private BitOutputStream bitOut;

    @BeforeEach
    void setUp() {
        byteOut = new ByteArrayOutputStream();
        bitOut = new BitOutputStream(byteOut);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (bitOut != null) {
            bitOut.close();
        }
    }

    // ==================== BitOutputStream Tests ====================

    @Test
    @DisplayName("writeBit zapisuje pojedyncze bity poprawnie")
    void testWriteSingleBits() throws IOException {
        // Zapisz 10101010 = 170
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.close();

        byte[] result = byteOut.toByteArray();
        assertEquals(1, result.length);
        assertEquals((byte) 0b10101010, result[0]);
    }

    @Test
    @DisplayName("writeByte zapisuje pełny bajt poprawnie")
    void testWriteByte() throws IOException {
        bitOut.writeByte((byte) 0xAB);
        bitOut.close();

        byte[] result = byteOut.toByteArray();
        assertEquals(1, result.length);
        assertEquals((byte) 0xAB, result[0]);
    }

    @Test
    @DisplayName("Mieszane writeBit i writeByte działa poprawnie")
    void testMixedBitAndByteWrites() throws IOException {
        // Zapisz 3 bity, potem bajt, potem 5 bitów
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeByte((byte) 0xFF); // 11111111
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.close();

        byte[] result = byteOut.toByteArray();
        assertEquals(2, result.length);
        // Pierwszy bajt: 101 11111 = 0b10111111 = 0xBF
        assertEquals((byte) 0b10111111, result[0]);
        // Drugi bajt: 111 01010 + padding 0 = 0b11101010
        assertEquals((byte) 0b11101010, result[1]);
    }

    @Test
    @DisplayName("getPaddingAdded zwraca prawidłową wartość")
    void testGetPaddingAdded() throws IOException {
        // Zapisz 3 bity - padding powinien wynosić 5
        bitOut.writeBit(1);
        bitOut.writeBit(0);
        bitOut.writeBit(1);
        bitOut.close();

        assertEquals(5, bitOut.getPaddingAdded());
    }

    @Test
    @DisplayName("getPaddingAdded zwraca 0 dla pełnych bajtów")
    void testGetPaddingAddedZero() throws IOException {
        bitOut.writeByte((byte) 0xFF);
        bitOut.close();

        assertEquals(0, bitOut.getPaddingAdded());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    @DisplayName("Padding jest poprawny dla różnej liczby bitów")
    void testPaddingForVariousBitCounts(int bitCount) throws IOException {
        for (int i = 0; i < bitCount; i++) {
            bitOut.writeBit(1);
        }
        bitOut.close();

        int expectedPadding = 8 - bitCount;
        assertEquals(expectedPadding, bitOut.getPaddingAdded());
    }

    @Test
    @DisplayName("writeBit rzuca wyjątek dla nieprawidłowej wartości")
    void testWriteBitInvalidValue() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> bitOut.writeBit(2));
        assertThrows(IllegalArgumentException.class, () -> bitOut.writeBit(-1));
    }

    // ==================== BitInputStream Tests ====================

    @Test
    @DisplayName("readBit odczytuje pojedyncze bity poprawnie")
    void testReadSingleBits() throws IOException {
        // Przygotuj dane: 10101010 = 170
        byte[] data = {(byte) 0b10101010};
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(data));

        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());

        bitIn.close();
    }

    @Test
    @DisplayName("readByte odczytuje pełny bajt poprawnie")
    void testReadByte() throws IOException {
        byte[] data = {(byte) 0xAB};
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(data));

        // readByte zwraca int 0-255 (unsigned)
        assertEquals(0xAB, bitIn.readByte());
        bitIn.close();
    }

    @Test
    @DisplayName("readBit zwraca -1 na końcu strumienia")
    void testReadBitEndOfStream() throws IOException {
        byte[] data = {(byte) 0xFF};
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(data));

        // Odczytaj 8 bitów
        for (int i = 0; i < 8; i++) {
            bitIn.readBit();
        }

        assertEquals(-1, bitIn.readBit());
        bitIn.close();
    }

    @Test
    @DisplayName("Mieszane readBit i readByte działa poprawnie")
    void testMixedBitAndByteReads() throws IOException {
        byte[] data = {(byte) 0b10111111, (byte) 0b11101010};
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(data));

        // Odczytaj 3 bity
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());

        // Odczytaj bajt (następnych 8 bitów) - readByte zwraca int 0-255
        assertEquals(0xFF, bitIn.readByte());

        // Odczytaj 5 bitów
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());
        assertEquals(1, bitIn.readBit());
        assertEquals(0, bitIn.readBit());

        bitIn.close();
    }

    // ==================== Roundtrip Tests ====================

    @Test
    @DisplayName("Roundtrip: zapisz i odczytaj te same bity")
    void testBitRoundtrip() throws IOException {
        // Zapisz losowy wzorzec bitów
        int[] bits = {1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0};

        bitOut = new BitOutputStream(byteOut);
        for (int bit : bits) {
            bitOut.writeBit(bit);
        }
        bitOut.close();

        // Odczytaj
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        for (int expectedBit : bits) {
            assertEquals(expectedBit, bitIn.readBit());
        }
        bitIn.close();
    }

    @Test
    @DisplayName("Roundtrip: zapisz i odczytaj te same bajty")
    void testByteRoundtrip() throws IOException {
        byte[] originalData = {0x00, (byte) 0xFF, 0x55, (byte) 0xAA, 0x12, 0x34};

        bitOut = new BitOutputStream(byteOut);
        for (byte b : originalData) {
            bitOut.writeByte(b);
        }
        bitOut.close();

        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        for (byte expected : originalData) {
            // readByte zwraca int 0-255, konwertujemy expected do unsigned
            assertEquals(Byte.toUnsignedInt(expected), bitIn.readByte());
        }
        bitIn.close();
    }

    @Test
    @DisplayName("Roundtrip: dane binarne (wszystkie możliwe bajty)")
    void testAllByteValuesRoundtrip() throws IOException {
        byte[] allBytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            allBytes[i] = (byte) i;
        }

        bitOut = new BitOutputStream(byteOut);
        for (byte b : allBytes) {
            bitOut.writeByte(b);
        }
        bitOut.close();

        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        for (int i = 0; i < 256; i++) {
            // readByte zwraca int 0-255 (unsigned)
            assertEquals(i, bitIn.readByte());
        }
        bitIn.close();
    }

    @Test
    @DisplayName("Empty stream: readBit zwraca -1")
    void testEmptyStreamReadBit() throws IOException {
        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(new byte[0]));
        assertEquals(-1, bitIn.readBit());
        bitIn.close();
    }

    @Test
    @DisplayName("Wiele bajtów zapisanych i odczytanych")
    void testMultipleBytes() throws IOException {
        for (int i = 0; i < 100; i++) {
            bitOut.writeByte((byte) i);
        }
        bitOut.close();

        BitInputStream bitIn = new BitInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        for (int i = 0; i < 100; i++) {
            assertEquals(i, bitIn.readByte());
        }
        bitIn.close();
    }

}
