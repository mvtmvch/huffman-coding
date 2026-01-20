package pl.edu.pw.ee.aisd2025zex5.huffman;

/**
 * Węzeł drzewa Huffmana.
 * 
 * Dwa typy węzłów:
 * 1. LIŚĆ - zawiera symbol (np. "A"), brak dzieci
 * 2. WĘZEŁ WEWNĘTRZNY - brak symbolu, ma lewe i prawe dziecko
 * 
 * Przykład drzewa dla "ABRACADABRA":
 * 
 *            (11)           ← węzeł wewnętrzny, freq = suma dzieci
 *           /    \
 *        A(5)    (6)        ← A to liść, (6) to węzeł wewnętrzny
 *               /   \
 *            R(2)   (4)
 *                  /   \
 *                (2)   B(2)
 *               /   \
 *             C(1)  D(1)
 * 
 * Implements Comparable - porównuje po częstości (frequency).
 * Dzięki temu CustomPriorityQueue zawsze zwraca węzeł o najmniejszej częstości.
 */
public class HuffmanNode implements Comparable<HuffmanNode> {

    private final String symbol;     // Symbol (tylko dla liścia, null dla węzłów wewnętrznych)
    private final int frequency;     // Częstość występowania (waga węzła)
    private final HuffmanNode left;  // Lewe dziecko (null dla liścia)
    private final HuffmanNode right; // Prawe dziecko (null dla liścia)

    /**
     * Konstruktor dla LIŚCIA (węzeł z symbolem).
     * 
     * @param symbol sekwencja znaków (np. "A" lub "AB" dla L=2)
     * @param frequency liczba wystąpień symbolu w tekście
     */
    public HuffmanNode(String symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    /**
     * Konstruktor dla WĘZŁA WEWNĘTRZNEGO (łączy dwa poddrzewa).
     * 
     * Częstość = suma częstości dzieci.
     * Symbol = null (węzeł wewnętrzny nie reprezentuje symbolu).
     * 
     * @param left lewe poddrzewo
     * @param right prawe poddrzewo
     */
    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbol = null;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    /**
     * Sprawdza czy węzeł jest liściem.
     * 
     * Liść nie ma dzieci - to jedyne miejsce gdzie zapisany jest symbol.
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getFrequency() {
        return frequency;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    /**
     * Porównuje węzły po częstości.
     * 
     * Mniejsza częstość = wyższy priorytet w kolejce.
     * Dzięki temu buildTree zawsze pobiera 2 najrzadsze węzły.
     */
    @Override
    public int compareTo(HuffmanNode other) {
        return Integer.compare(this.frequency, other.frequency);
    }
}
