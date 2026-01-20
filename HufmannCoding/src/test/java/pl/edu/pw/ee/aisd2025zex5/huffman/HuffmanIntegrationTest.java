package pl.edu.pw.ee.aisd2025zex5.huffman;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.edu.pw.ee.aisd2025zex5.exception.HuffmanException;

class HuffmanIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Round-trip: pusty plik (L=1)")
    void testRoundTripEmptyFile() throws IOException {
        Path input = tempDir.resolve("in.bin");
        Path compressed = tempDir.resolve("out.huff");
        Path restored = tempDir.resolve("restored.bin");

        Files.write(input, new byte[0]);

        HuffmanCompressor c = new HuffmanCompressor();
        HuffmanDecompressor d = new HuffmanDecompressor();

        assertDoesNotThrow(() -> c.compress(input.toString(), compressed.toString(), 1));
        assertDoesNotThrow(() -> d.decompress(compressed.toString(), restored.toString()));

        assertArrayEquals(Files.readAllBytes(input), Files.readAllBytes(restored));
    }

    @Test
    @DisplayName("Round-trip: tekst (L=1)")
    void testRoundTripTextL1() throws IOException {
        Path input = tempDir.resolve("in.txt");
        Path compressed = tempDir.resolve("out.huff");
        Path restored = tempDir.resolve("restored.txt");

        byte[] data = "abracadabra abracadabra!".getBytes();
        Files.write(input, data);

        HuffmanCompressor c = new HuffmanCompressor();
        HuffmanDecompressor d = new HuffmanDecompressor();

        c.compress(input.toString(), compressed.toString(), 1);
        d.decompress(compressed.toString(), restored.toString());

        assertArrayEquals(data, Files.readAllBytes(restored));
    }

    @Test
    @DisplayName("Round-trip: binarka losowa, długość niepodzielna przez L (L=4)")
    void testRoundTripRandomBinaryL4() throws IOException {
        Path input = tempDir.resolve("in.bin");
        Path compressed = tempDir.resolve("out.huff");
        Path restored = tempDir.resolve("restored.bin");

        byte[] data = new byte[1025]; // celowo niepodzielne przez 4
        new Random(12345).nextBytes(data);
        Files.write(input, data);

        HuffmanCompressor c = new HuffmanCompressor();
        HuffmanDecompressor d = new HuffmanDecompressor();

        c.compress(input.toString(), compressed.toString(), 4);
        d.decompress(compressed.toString(), restored.toString());

        assertArrayEquals(data, Files.readAllBytes(restored));
    }

    @Test
    @DisplayName("Round-trip: jeden unikalny symbol (L=3, brak bytePadding)")
    void testRoundTripSingleSymbolL3() throws IOException {
        Path input = tempDir.resolve("in.bin");
        Path compressed = tempDir.resolve("out.huff");
        Path restored = tempDir.resolve("restored.bin");

        byte[] data = new byte[3000]; // podzielne przez 3
        for (int i = 0; i < data.length; i++) {
            data[i] = 0x7F;
        }
        Files.write(input, data);

        HuffmanCompressor c = new HuffmanCompressor();
        HuffmanDecompressor d = new HuffmanDecompressor();

        c.compress(input.toString(), compressed.toString(), 3);
        d.decompress(compressed.toString(), restored.toString());

        assertArrayEquals(data, Files.readAllBytes(restored));
    }

    @Test
    @DisplayName("Dekompresja: ucięty/uszkodzony plik -> HuffmanException")
    void testDecompressTruncatedFileThrows() throws IOException {
        Path input = tempDir.resolve("in.bin");
        Path compressed = tempDir.resolve("out.huff");
        Path truncated = tempDir.resolve("truncated.huff");
        Path restored = tempDir.resolve("restored.bin");

        byte[] data = new byte[2048];
        new Random(7).nextBytes(data);
        Files.write(input, data);

        HuffmanCompressor c = new HuffmanCompressor();
        HuffmanDecompressor d = new HuffmanDecompressor();

        c.compress(input.toString(), compressed.toString(), 2);

        byte[] full = Files.readAllBytes(compressed);
        byte[] cut = new byte[Math.max(0, full.length - 1)];
        System.arraycopy(full, 0, cut, 0, cut.length);
        Files.write(truncated, cut);

        assertThrows(HuffmanException.class, () -> d.decompress(truncated.toString(), restored.toString()));
    }

    @Test
    @DisplayName("Dekompresja: nieprawidłowy nagłówek (L=0) -> HuffmanException")
    void testInvalidHeaderThrows() throws IOException {
        Path bad = tempDir.resolve("bad.huff");
        Path restored = tempDir.resolve("restored.bin");

        Files.write(bad, new byte[] {0, 0, 0}); // L=0 (nielegalne)

        HuffmanDecompressor d = new HuffmanDecompressor();
        assertThrows(HuffmanException.class, () -> d.decompress(bad.toString(), restored.toString()));
    }
}
