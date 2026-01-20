package pl.edu.pw.ee.aisd2025zex5.exception;

/**
 * Custom exception for Huffman compression/decompression errors.
 */
public class HuffmanException extends RuntimeException {

    public HuffmanException(String message) {
        super(message);
    }

    public HuffmanException(String message, Throwable cause) {
        super(message, cause);
    }
}
