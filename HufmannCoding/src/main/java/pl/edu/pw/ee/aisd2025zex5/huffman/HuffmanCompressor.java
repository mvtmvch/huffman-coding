package pl.edu.pw.ee.aisd2025zex5.huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import pl.edu.pw.ee.aisd2025zex5.datastructures.CustomHashMap;
import pl.edu.pw.ee.aisd2025zex5.datastructures.CustomPriorityQueue;
import pl.edu.pw.ee.aisd2025zex5.datastructures.Entry;
import pl.edu.pw.ee.aisd2025zex5.exception.HuffmanException;
import pl.edu.pw.ee.aisd2025zex5.io.BitOutputStream;

public class HuffmanCompressor {

    // Czyta do pełnego bloku L (chyba że EOF). To naprawia błąd z read(buffer)<L "w środku".
    private static int readFullBlock(FileInputStream fis, byte[] buf) throws IOException {
        int off = 0;
        while (off < buf.length) {
            int r = fis.read(buf, off, buf.length - off);
            if (r == -1) {
                return (off == 0) ? -1 : off;
            }
            off += r;
        }
        return off;
    }

    public void compress(String sourcePath, String destPath, int L) {
        try {
            Objects.requireNonNull(sourcePath, "sourcePath nie może być null");
            Objects.requireNonNull(destPath, "destPath nie może być null");

            if (L < 1 || L > 255) {
                throw new HuffmanException("Parametr -l musi być w zakresie 1..255, otrzymano: " + L);
            }

            AnalysisResult analysis = analyzeFile(sourcePath, L);
            CustomHashMap<String, Integer> frequencies = analysis.frequencies;

            // pusty plik
            if (frequencies.size() == 0) {
                try (FileOutputStream fos = new FileOutputStream(destPath)) {
                    fos.write(L);
                    fos.write(0);
                    fos.write(0);
                }
                return;
            }

            HuffmanNode root = buildTree(frequencies);

            CustomHashMap<String, String> codes = new CustomHashMap<>();
            generateCodes(root, "", codes);

            // bytePadding: ile bajtów dopisaliśmy zerami w ostatnim symbolu
            int remainder = (int) (analysis.totalBytes % L);
            int bytePadding = (remainder == 0) ? 0 : (L - remainder);

            // bitPadding: liczony z góry (bez buforowania całego pliku w RAM)
            long treeBits = calculateTreeBitLength(root, L);
            long dataBits = calculateDataBitLength(frequencies, codes);
            long totalBits = treeBits + dataBits;
            int bitPadding = (int) ((8 - (totalBits % 8)) % 8);

            try (FileOutputStream fos = new FileOutputStream(destPath)) {
                // nagłówek
                fos.write(L);
                fos.write(bitPadding);
                fos.write(bytePadding);

                try (BitOutputStream bitOut = new BitOutputStream(fos)) {
                    writeTree(root, bitOut, L);
                    encodeFile(sourcePath, bitOut, codes, L);
                }
            }

        } catch (IOException e) {
            throw new HuffmanException("Błąd podczas kompresji: " + e.getMessage(), e);
        }
    }

    private AnalysisResult analyzeFile(String path, int L) throws IOException {
        CustomHashMap<String, Integer> frequencies = new CustomHashMap<>();
        long totalBytes = 0;

        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] buffer = new byte[L];
            int bytesRead;

            while ((bytesRead = readFullBlock(fis, buffer)) != -1) {
                totalBytes += bytesRead;

                if (bytesRead < L) {
                    for (int i = bytesRead; i < L; i++) {
                        buffer[i] = 0;
                    }
                }

                String symbol = new String(buffer, StandardCharsets.ISO_8859_1);

                Integer count = frequencies.get(symbol);
                if (count == null) {
                    frequencies.put(symbol, 1);
                } else {
                    frequencies.put(symbol, count + 1);
                }
            }
        }

        return new AnalysisResult(frequencies, totalBytes);
    }

    private HuffmanNode buildTree(CustomHashMap<String, Integer> frequencies) {
        CustomPriorityQueue<HuffmanNode> queue = new CustomPriorityQueue<>();

        for (Entry<String, Integer> entry : frequencies.entrySet()) {
            queue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            queue.add(new HuffmanNode(left, right));
        }

        return queue.poll();
    }

    private void generateCodes(HuffmanNode node, String code, CustomHashMap<String, String> codes) {
        if (node == null) return;

        if (node.isLeaf()) {
            codes.put(node.getSymbol(), code.isEmpty() ? "0" : code);
            return;
        }

        generateCodes(node.getLeft(), code + "0", codes);
        generateCodes(node.getRight(), code + "1", codes);
    }

    private long calculateTreeBitLength(HuffmanNode node, int L) {
        if (node == null) return 0;
        if (node.isLeaf()) return 1L + 8L * L;
        return 1L + calculateTreeBitLength(node.getLeft(), L) + calculateTreeBitLength(node.getRight(), L);
    }

    private long calculateDataBitLength(CustomHashMap<String, Integer> frequencies,
                                        CustomHashMap<String, String> codes) {
        long bits = 0;
        for (Entry<String, Integer> entry : frequencies.entrySet()) {
            String symbol = entry.getKey();
            Integer freq = entry.getValue();
            String code = codes.get(symbol);
            if (code == null) {
                throw new HuffmanException("Brak kodu dla symbolu przy liczeniu długości danych");
            }
            bits += (long) freq * (long) code.length();
        }
        return bits;
    }

    private void writeTree(HuffmanNode node, BitOutputStream out, int L) throws IOException {
        if (node.isLeaf()) {
            out.writeBit(1);

            byte[] symbolBytes = node.getSymbol().getBytes(StandardCharsets.ISO_8859_1);
            if (symbolBytes.length != L) {
                byte[] fixed = new byte[L];
                int len = Math.min(symbolBytes.length, L);
                System.arraycopy(symbolBytes, 0, fixed, 0, len);
                symbolBytes = fixed;
            }

            for (byte b : symbolBytes) {
                out.writeByte(b);
            }
        } else {
            out.writeBit(0);
            writeTree(node.getLeft(), out, L);
            writeTree(node.getRight(), out, L);
        }
    }

    private void encodeFile(String path, BitOutputStream out,
                            CustomHashMap<String, String> codes, int L) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] buffer = new byte[L];
            int bytesRead;

            while ((bytesRead = readFullBlock(fis, buffer)) != -1) {
                if (bytesRead < L) {
                    for (int i = bytesRead; i < L; i++) {
                        buffer[i] = 0;
                    }
                }

                String symbol = new String(buffer, StandardCharsets.ISO_8859_1);
                String code = codes.get(symbol);
                if (code == null) {
                    throw new HuffmanException("Nie znaleziono kodu dla symbolu!");
                }

                for (int i = 0; i < code.length(); i++) {
                    out.writeBit(code.charAt(i) == '1' ? 1 : 0);
                }
            }
        }
    }

    private static class AnalysisResult {
        final CustomHashMap<String, Integer> frequencies;
        final long totalBytes;

        AnalysisResult(CustomHashMap<String, Integer> frequencies, long totalBytes) {
            this.frequencies = frequencies;
            this.totalBytes = totalBytes;
        }
    }
}
