package pl.edu.pw.ee.aisd2025zex5.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testy dla HuffmanException.
 */
class HuffmanExceptionTest {

    @Test
    @DisplayName("Konstruktor z wiadomością")
    void testMessageConstructor() {
        String message = "Test error message";
        HuffmanException ex = new HuffmanException(message);

        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Konstruktor z wiadomością i przyczyną")
    void testMessageAndCauseConstructor() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Original cause");
        
        HuffmanException ex = new HuffmanException(message, cause);

        assertEquals(message, ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("HuffmanException jest RuntimeException")
    void testIsRuntimeException() {
        HuffmanException ex = new HuffmanException("test");
        
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    @DisplayName("Można rzucić i złapać HuffmanException")
    void testThrowAndCatch() {
        String message = "Expected error";
        
        HuffmanException caught = assertThrows(HuffmanException.class, () -> {
            throw new HuffmanException(message);
        });
        
        assertEquals(message, caught.getMessage());
    }

    @Test
    @DisplayName("Wiadomość null jest dozwolona")
    void testNullMessage() {
        HuffmanException ex = new HuffmanException(null);
        
        assertNull(ex.getMessage());
    }

    @Test
    @DisplayName("Przyczyna null jest dozwolona")
    void testNullCause() {
        HuffmanException ex = new HuffmanException("message", null);
        
        assertEquals("message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Chain wyjątków działa poprawnie")
    void testExceptionChaining() {
        IOException originalCause = new IOException("IO error");
        HuffmanException wrapper = new HuffmanException("Compression failed", originalCause);
        
        assertEquals("Compression failed", wrapper.getMessage());
        assertEquals("IO error", wrapper.getCause().getMessage());
        assertTrue(wrapper.getCause() instanceof IOException);
    }

    private static class IOException extends Exception {
        IOException(String message) {
            super(message);
        }
    }
}
