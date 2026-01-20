package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.File;

import pl.edu.pw.ee.aisd2025zex5.exception.HuffmanException;

public class ArgsValidator {

    private String mode;
    private String sourcePath;
    private String destPath;
    private int sequenceLength = 1;

    public void parseAndValidate(String[] args) {
        parseArgs(args);
        validate();
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-m":
                    mode = getNextValue(args, i, "-m");
                    i++;
                    break;

                case "-s":
                    sourcePath = getNextValue(args, i, "-s");
                    i++;
                    break;

                case "-d":
                    destPath = getNextValue(args, i, "-d");
                    i++;
                    break;

                case "-l":
                    String lengthStr = getNextValue(args, i, "-l");
                    try {
                        sequenceLength = Integer.parseInt(lengthStr);
                    } catch (NumberFormatException e) {
                        throw new HuffmanException("Parametr -l musi być liczbą całkowitą, otrzymano: " + lengthStr);
                    }
                    i++;
                    break;

                default:
                    // STRICT: nieznane flagi / osierocone wartości = błąd
                    if (arg.startsWith("-")) {
                        throw new HuffmanException("Nieznana flaga: " + arg);
                    }
                    throw new HuffmanException("Nieoczekiwana wartość bez flagi: " + arg);
            }
        }
    }

    private String getNextValue(String[] args, int currentIndex, String flagName) {
        int valueIndex = currentIndex + 1;

        if (valueIndex >= args.length) {
            throw new HuffmanException("Brak wartości dla flagi " + flagName);
        }

        String value = args[valueIndex];

        if (value.startsWith("-")) {
            throw new HuffmanException("Flaga " + flagName + " wymaga wartości, otrzymano inną flagę: " + value);
        }

        return value;
    }

    private void validate() {
        if (mode == null) {
            throw new HuffmanException("Wymagany parametr -m (tryb: comp lub decomp)");
        }
        if (!mode.equals("comp") && !mode.equals("decomp")) {
            throw new HuffmanException("Tryb musi być 'comp' lub 'decomp', otrzymano: " + mode);
        }

        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new HuffmanException("Wymagany parametr -s (ścieżka do pliku źródłowego)");
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new HuffmanException("Plik źródłowy nie istnieje: " + sourcePath);
        }
        if (!sourceFile.isFile()) {
            throw new HuffmanException("Ścieżka źródłowa nie jest plikiem: " + sourcePath);
        }

        if (destPath == null || destPath.isEmpty()) {
            throw new HuffmanException("Wymagany parametr -d (ścieżka do pliku docelowego)");
        }

        if (sequenceLength < 1) {
            throw new HuffmanException("Długość sekwencji (-l) musi być >= 1, otrzymano: " + sequenceLength);
        }
        if (sequenceLength > 255) {
            throw new HuffmanException("Długość sekwencji (-l) nie może przekraczać 255, otrzymano: " + sequenceLength);
        }
    }

    public String getMode() {
        return mode;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public boolean isCompression() {
        return "comp".equals(mode);
    }

    public boolean isDecompression() {
        return "decomp".equals(mode);
    }
}
