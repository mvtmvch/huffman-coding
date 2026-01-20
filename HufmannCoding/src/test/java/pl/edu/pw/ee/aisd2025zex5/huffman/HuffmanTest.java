package pl.edu.pw.ee.aisd2025zex5.huffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testy integracyjne dla HuffmanCompressor i HuffmanDecompressor.
 */
class HuffmanTest {

    private Path tempDir;
    private Path inputFile;
    private Path compressedFile;
    private Path decompressedFile;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("huffman_test");
        inputFile = tempDir.resolve("input.txt");
        compressedFile = tempDir.resolve("compressed.huff");
        decompressedFile = tempDir.resolve("decompressed.txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Usuń pliki tymczasowe
        Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignoruj
                    }
                });
    }

    // ==================== Podstawowe testy ====================

    @Test
    @DisplayName("Roundtrip: prosty tekst ASCII")
    void testSimpleAsciiText() throws IOException {
        String content = "Hello, World!";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    @DisplayName("Roundtrip: różne wartości L")
    void testDifferentLValues(int L) throws IOException {
        // Tekst wystarczająco długi dla różnych L
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("ABCDEFGHIJKLMNOP");
        }
        Files.write(inputFile, sb.toString().getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), L);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed, "Roundtrip powinien zachować dane dla L=" + L);
    }

    @Test
    @DisplayName("Roundtrip: jeden znak powtórzony wielokrotnie")
    void testSingleRepeatedChar() throws IOException {
        String content = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    @Test
    @DisplayName("Roundtrip: dwa różne znaki")
    void testTwoDifferentChars() throws IOException {
        String content = "ABABABABABABABABABABABABABABAB";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    // ==================== Testy danych binarnych ====================

    @Test
    @DisplayName("Roundtrip: wszystkie możliwe bajty (0-255)")
    void testAllByteValues() throws IOException {
        byte[] allBytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            allBytes[i] = (byte) i;
        }
        Files.write(inputFile, allBytes);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(allBytes, decompressed);
    }

    @Test
    @DisplayName("Roundtrip: losowe dane binarne")
    void testRandomBinaryData() throws IOException {
        Random random = new Random(12345);
        byte[] randomData = new byte[10000];
        random.nextBytes(randomData);
        Files.write(inputFile, randomData);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(randomData, decompressed);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    @DisplayName("Roundtrip: losowe dane z różnymi L")
    void testRandomDataWithDifferentL(int L) throws IOException {
        Random random = new Random(L * 12345);
        // Dane muszą być podzielne przez L
        int size = L * 1000;
        byte[] randomData = new byte[size];
        random.nextBytes(randomData);
        Files.write(inputFile, randomData);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), L);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(randomData, decompressed, "Roundtrip powinien zachować dane dla L=" + L);
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Roundtrip: bardzo krótki plik (1 bajt)")
    void testSingleByteFile() throws IOException {
        Files.write(inputFile, new byte[]{0x42}); // 'B'

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(new byte[]{0x42}, decompressed);
    }

    @Test
    @DisplayName("Roundtrip: plik z samymi zerami")
    void testAllZeroBytes() throws IOException {
        byte[] zeros = new byte[1000];
        Files.write(inputFile, zeros);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(zeros, decompressed);
    }

    @Test
    @DisplayName("Roundtrip: plik z samymi 0xFF")
    void testAllFFBytes() throws IOException {
        byte[] ffs = new byte[1000];
        for (int i = 0; i < 1000; i++) {
            ffs[i] = (byte) 0xFF;
        }
        Files.write(inputFile, ffs);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(ffs, decompressed);
    }

    @Test
    @DisplayName("Roundtrip: naprzemiennie 0x00 i 0xFF")
    void testAlternatingBytes() throws IOException {
        byte[] alternating = new byte[1000];
        for (int i = 0; i < 1000; i++) {
            alternating[i] = (i % 2 == 0) ? (byte) 0x00 : (byte) 0xFF;
        }
        Files.write(inputFile, alternating);

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(alternating, decompressed);
    }

    // ==================== Testy kompresji ====================

    @Test
    @DisplayName("Kompresja: tekst z dużą redundancją jest dobrze kompresowany")
    void testHighRedundancyCompression() throws IOException {
        // Bardzo redundantny tekst
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("AAAA");
        }
        Files.write(inputFile, sb.toString().getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        long originalSize = Files.size(inputFile);
        long compressedSize = Files.size(compressedFile);

        // Dla jednego znaku kompresja powinna być bardzo dobra
        assertTrue(compressedSize < originalSize / 4,
                "Kompresja powinna być lepsza niż 75% dla jednego powtarzającego się znaku");
    }

    // ==================== Testy wielobajtowych symboli ====================

    @Test
    @DisplayName("L=2: poprawna kompresja i dekompresja")
    void testL2Compression() throws IOException {
        // Parzysty rozmiar dla L=2
        String content = "AABBCCDDAABBCCDD";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 2);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    @Test
    @DisplayName("L=3: poprawna kompresja i dekompresja")
    void testL3Compression() throws IOException {
        // Rozmiar podzielny przez 3
        String content = "ABCDEFABCDEFABCDEF";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 3);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    @Test
    @DisplayName("L=4: poprawna kompresja i dekompresja")
    void testL4Compression() throws IOException {
        // Rozmiar podzielny przez 4
        String content = "ABCDABCDABCDABCD";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 4);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }

    // ==================== Testy konsystencji ====================

    @Test
    @DisplayName("Wielokrotna kompresja daje ten sam wynik")
    void testDeterministicCompression() throws IOException {
        String content = "Test deterministic compression ABCDEF123456";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        Path compressed1 = tempDir.resolve("compressed1.huff");
        Path compressed2 = tempDir.resolve("compressed2.huff");

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressed1.toString(), 1);
        compressor.compress(inputFile.toString(), compressed2.toString(), 1);

        byte[] bytes1 = Files.readAllBytes(compressed1);
        byte[] bytes2 = Files.readAllBytes(compressed2);

        assertArrayEquals(bytes1, bytes2, "Dwie kompresje tego samego pliku powinny dać ten sam wynik");
    }

    @Test
    @DisplayName("Tekst w różnych językach (polski)")
    void testPolishText() throws IOException {
        String content = "Zażółć gęślą jaźń! Pchnąć w tę łódź jeża lub osiem skrzyń fig.";
        Files.write(inputFile, content.getBytes(StandardCharsets.ISO_8859_1));

        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFile.toString(), compressedFile.toString(), 1);

        HuffmanDecompressor decompressor = new HuffmanDecompressor();
        decompressor.decompress(compressedFile.toString(), decompressedFile.toString());

        byte[] original = Files.readAllBytes(inputFile);
        byte[] decompressed = Files.readAllBytes(decompressedFile);
        assertArrayEquals(original, decompressed);
    }
}
