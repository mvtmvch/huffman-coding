package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.edu.pw.ee.aisd2025zex5.exception.HuffmanException;

class ArgsValidatorTest {

    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(tempFile.toPath(), "test content");
    }

    @Test
    @DisplayName("Poprawne argumenty kompresji")
    void testValidCompressionArgs() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "2"};

        assertDoesNotThrow(() -> validator.parseAndValidate(args));

        assertEquals("comp", validator.getMode());
        assertEquals(tempFile.getAbsolutePath(), validator.getSourcePath());
        assertEquals("output.huff", validator.getDestPath());
        assertEquals(2, validator.getSequenceLength());
        assertTrue(validator.isCompression());
        assertFalse(validator.isDecompression());
    }

    @Test
    @DisplayName("Poprawne argumenty dekompresji")
    void testValidDecompressionArgs() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "decomp", "-s", tempFile.getAbsolutePath(), "-d", "restored.txt"};

        assertDoesNotThrow(() -> validator.parseAndValidate(args));

        assertEquals("decomp", validator.getMode());
        assertTrue(validator.isDecompression());
        assertFalse(validator.isCompression());
    }

    @Test
    @DisplayName("Domyślna wartość sequenceLength = 1")
    void testDefaultSequenceLength() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff"};

        validator.parseAndValidate(args);
        assertEquals(1, validator.getSequenceLength());
    }

    @Test
    @DisplayName("Brak parametru -m rzuca wyjątek")
    void testMissingMode() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-s", tempFile.getAbsolutePath(), "-d", "output.huff"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().contains("-m"));
    }

    @Test
    @DisplayName("Nieprawidłowy tryb rzuca wyjątek")
    void testInvalidMode() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "invalid", "-s", tempFile.getAbsolutePath(), "-d", "output.huff"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().contains("comp") || ex.getMessage().contains("decomp"));
    }

    @Test
    @DisplayName("Brak parametru -s rzuca wyjątek")
    void testMissingSourcePath() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-d", "output.huff"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().contains("-s"));
    }

    @Test
    @DisplayName("Nieistniejący plik źródłowy rzuca wyjątek")
    void testNonExistentSourceFile() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", "/nonexistent/file.txt", "-d", "output.huff"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().toLowerCase().contains("nie istnieje") || ex.getMessage().toLowerCase().contains("nie istnie"));
    }

    @Test
    @DisplayName("Katalog jako plik źródłowy rzuca wyjątek")
    void testDirectoryAsSourceFile() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempDir.toString(), "-d", "output.huff"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().toLowerCase().contains("nie jest plikiem"));
    }

    @Test
    @DisplayName("Brak parametru -d rzuca wyjątek")
    void testMissingDestPath() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath()};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().contains("-d"));
    }

    @Test
    @DisplayName("sequenceLength = 0 rzuca wyjątek")
    void testSequenceLengthZero() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "0"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }

    @Test
    @DisplayName("sequenceLength > 255 rzuca wyjątek")
    void testSequenceLengthTooLarge() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "256"};

        HuffmanException ex = assertThrows(HuffmanException.class,
                () -> validator.parseAndValidate(args));
        assertTrue(ex.getMessage().contains("255"));
    }

    @Test
    @DisplayName("sequenceLength nie-liczbowy rzuca wyjątek")
    void testSequenceLengthNotNumber() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "abc"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }

    @Test
    @DisplayName("Brak wartości po fladze rzuca wyjątek")
    void testMissingValueAfterFlag() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }

    @Test
    @DisplayName("Flaga jako wartość rzuca wyjątek")
    void testFlagAsValue() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "-s"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }

    @Test
    @DisplayName("Argumenty w różnej kolejności")
    void testArgumentsInDifferentOrder() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-l", "3", "-d", "output.huff", "-s", tempFile.getAbsolutePath(), "-m", "comp"};

        assertDoesNotThrow(() -> validator.parseAndValidate(args));
        assertEquals("comp", validator.getMode());
        assertEquals(3, validator.getSequenceLength());
    }

    @Test
    @DisplayName("Nieznana flaga rzuca wyjątek (STRICT)")
    void testUnknownFlagsThrow() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "--unknown"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }

    @Test
    @DisplayName("sequenceLength = 255 jest akceptowany")
    void testSequenceLengthMax() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "255"};

        assertDoesNotThrow(() -> validator.parseAndValidate(args));
        assertEquals(255, validator.getSequenceLength());
    }

    @Test
    @DisplayName("sequenceLength ujemny rzuca wyjątek")
    void testNegativeSequenceLength() {
        ArgsValidator validator = new ArgsValidator();
        String[] args = {"-m", "comp", "-s", tempFile.getAbsolutePath(), "-d", "output.huff", "-l", "-5"};

        assertThrows(HuffmanException.class, () -> validator.parseAndValidate(args));
    }
}
