package pl.edu.pw.ee.aisd2025zex5;

import pl.edu.pw.ee.aisd2025zex5.huffman.HuffmanCompressor;
import pl.edu.pw.ee.aisd2025zex5.huffman.HuffmanDecompressor;
import pl.edu.pw.ee.aisd2025zex5.utils.ArgsValidator;


public class Main {

    public static void main(String[] args) {
        try {
            // 1. Parsuj i waliduj argumenty
            ArgsValidator validator = new ArgsValidator();
            validator.parseAndValidate(args);
            
            String sourcePath = validator.getSourcePath();
            String destPath = validator.getDestPath();
            int L = validator.getSequenceLength();
            
            // 2. Uruchom odpowiedni tryb
            if (validator.isCompression()) {
                System.out.println("Tryb: KOMPRESJA");
                System.out.println("  Źródło:    " + sourcePath);
                System.out.println("  Cel:       " + destPath);
                System.out.println("  Sekwencja: " + L);
                System.out.println();
                
                HuffmanCompressor compressor = new HuffmanCompressor();
                compressor.compress(sourcePath, destPath, L);
                
                System.out.println("Kompresja się powiodła");
                
            } else {
                System.out.println("Tryb: DEKOMPRESJA");
                System.out.println("  Źródło: " + sourcePath);
                System.out.println("  Cel:    " + destPath);
                System.out.println();
                
                HuffmanDecompressor decompressor = new HuffmanDecompressor();
                decompressor.decompress(sourcePath, destPath);
                
                System.out.println("Dekompresja się powiodła");
            }
            
        } catch (Exception e) {
            System.err.println("BŁĄD: " + e.getMessage());
            System.exit(1);
        }
    }
}
