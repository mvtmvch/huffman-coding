package pl.edu.pw.ee.aisd2025zex5.huffman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testy dla HuffmanNode.
 */
class HuffmanNodeTest {

    @Test
    @DisplayName("HuffmanNode liść - poprawne tworzenie")
    void testLeafNodeCreation() {
        HuffmanNode node = new HuffmanNode("A", 10);

        assertEquals("A", node.getSymbol());
        assertEquals(10, node.getFrequency());
        assertTrue(node.isLeaf());
        assertNull(node.getLeft());
        assertNull(node.getRight());
    }

    @Test
    @DisplayName("HuffmanNode wewnętrzny - poprawne tworzenie")
    void testInternalNodeCreation() {
        HuffmanNode left = new HuffmanNode("A", 5);
        HuffmanNode right = new HuffmanNode("B", 3);
        HuffmanNode parent = new HuffmanNode(left, right);

        assertNull(parent.getSymbol());
        assertEquals(8, parent.getFrequency());
        assertFalse(parent.isLeaf());
        assertSame(left, parent.getLeft());
        assertSame(right, parent.getRight());
    }

    @Test
    @DisplayName("HuffmanNode compareTo - porównanie po częstotliwości")
    void testNodeComparison() {
        HuffmanNode node1 = new HuffmanNode("A", 5);
        HuffmanNode node2 = new HuffmanNode("B", 10);
        HuffmanNode node3 = new HuffmanNode("C", 5);

        assertTrue(node1.compareTo(node2) < 0);
        assertTrue(node2.compareTo(node1) > 0);
        assertEquals(0, node1.compareTo(node3));
    }

    @Test
    @DisplayName("HuffmanNode - wielopoziomowe drzewo")
    void testMultiLevelTree() {
        // Liście
        HuffmanNode a = new HuffmanNode("A", 5);
        HuffmanNode b = new HuffmanNode("B", 9);
        HuffmanNode c = new HuffmanNode("C", 12);
        HuffmanNode d = new HuffmanNode("D", 13);

        // Poziom 1
        HuffmanNode ab = new HuffmanNode(a, b);
        HuffmanNode cd = new HuffmanNode(c, d);

        // Korzeń
        HuffmanNode root = new HuffmanNode(ab, cd);

        assertEquals(39, root.getFrequency());
        assertEquals(14, ab.getFrequency());
        assertEquals(25, cd.getFrequency());
        assertFalse(root.isLeaf());
    }

    @Test
    @DisplayName("HuffmanNode - symbole wielobajtowe")
    void testMultiByteSymbols() {
        HuffmanNode node = new HuffmanNode("ABC", 100);

        assertEquals("ABC", node.getSymbol());
        assertEquals(100, node.getFrequency());
        assertTrue(node.isLeaf());
    }

    @Test
    @DisplayName("HuffmanNode - symbole binarne")
    void testBinarySymbols() {
        // Symbol zawierający wszystkie bajty 0-255
        for (int i = 0; i < 256; i++) {
            String symbol = String.valueOf((char) i);
            HuffmanNode node = new HuffmanNode(symbol, i + 1);
            
            assertEquals(symbol, node.getSymbol());
            assertEquals(i + 1, node.getFrequency());
        }
    }

    @Test
    @DisplayName("HuffmanNode - duża częstotliwość")
    void testLargeFrequency() {
        HuffmanNode node = new HuffmanNode("X", Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, node.getFrequency());
    }

    @Test
    @DisplayName("HuffmanNode - minimalna częstotliwość")
    void testMinimalFrequency() {
        HuffmanNode node = new HuffmanNode("X", 1);

        assertEquals(1, node.getFrequency());
    }

    @Test
    @DisplayName("HuffmanNode - pusty symbol")
    void testEmptySymbol() {
        HuffmanNode node = new HuffmanNode("", 10);

        assertEquals("", node.getSymbol());
        assertTrue(node.isLeaf());
    }
}
