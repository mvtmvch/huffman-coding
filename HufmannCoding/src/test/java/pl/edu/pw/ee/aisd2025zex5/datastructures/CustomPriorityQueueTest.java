package pl.edu.pw.ee.aisd2025zex5.datastructures;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testy dla CustomPriorityQueue.
 */
class CustomPriorityQueueTest {

    private CustomPriorityQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new CustomPriorityQueue<>();
    }

    @Test
    @DisplayName("add i poll - podstawowe operacje")
    void testAddAndPoll() {
        queue.add(3);
        queue.add(1);
        queue.add(2);

        assertEquals(1, queue.poll()); // Min-heap - najmniejszy pierwszy
        assertEquals(2, queue.poll());
        assertEquals(3, queue.poll());
    }

    @Test
    @DisplayName("poll zwraca null dla pustej kolejki")
    void testPollEmpty() {
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("size zwraca prawidłową liczbę elementów")
    void testSize() {
        assertEquals(0, queue.size());

        queue.add(1);
        assertEquals(1, queue.size());

        queue.add(2);
        assertEquals(2, queue.size());

        queue.poll();
        assertEquals(1, queue.size());
    }

    @Test
    @DisplayName("isEmpty działa poprawnie")
    void testIsEmpty() {
        assertTrue(queue.isEmpty());

        queue.add(1);
        assertFalse(queue.isEmpty());

        queue.poll();
        assertTrue(queue.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100, 500, 1000})
    @DisplayName("Utrzymuje właściwość min-heap dla wielu elementów")
    void testMinHeapProperty(int count) {
        Random random = new Random(12345);
        for (int i = 0; i < count; i++) {
            queue.add(random.nextInt(10000));
        }

        int previous = Integer.MIN_VALUE;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            assertTrue(current >= previous, "Elementy powinny być w kolejności rosnącej");
            previous = current;
        }
    }

    @Test
    @DisplayName("Obsługuje duplikaty")
    void testDuplicates() {
        queue.add(5);
        queue.add(5);
        queue.add(5);

        assertEquals(3, queue.size());
        assertEquals(5, queue.poll());
        assertEquals(5, queue.poll());
        assertEquals(5, queue.poll());
    }

    @Test
    @DisplayName("Obsługuje ujemne liczby")
    void testNegativeNumbers() {
        queue.add(-5);
        queue.add(0);
        queue.add(5);
        queue.add(-10);

        assertEquals(-10, queue.poll());
        assertEquals(-5, queue.poll());
        assertEquals(0, queue.poll());
        assertEquals(5, queue.poll());
    }

    @Test
    @DisplayName("Zachowuje stabilność przy równych elementach")
    void testStabilityWithEqualElements() {
        for (int i = 0; i < 10; i++) {
            queue.add(0);
        }

        assertEquals(10, queue.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(0, queue.poll());
        }
        assertTrue(queue.isEmpty());
    }

    // Test z własnym Comparable
    @Test
    @DisplayName("Działa z niestandardowym Comparable")
    void testCustomComparable() {
        CustomPriorityQueue<TestClass> customQueue = new CustomPriorityQueue<>();

        customQueue.add(new TestClass("C", 3));
        customQueue.add(new TestClass("A", 1));
        customQueue.add(new TestClass("B", 2));

        assertEquals("A", customQueue.poll().name);
        assertEquals("B", customQueue.poll().name);
        assertEquals("C", customQueue.poll().name);
    }

    private static class TestClass implements Comparable<TestClass> {
        String name;
        int priority;

        TestClass(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public int compareTo(TestClass other) {
            return Integer.compare(this.priority, other.priority);
        }
    }

    @Test
    @DisplayName("Obsługuje Integer.MAX_VALUE i MIN_VALUE")
    void testExtremValues() {
        queue.add(Integer.MAX_VALUE);
        queue.add(Integer.MIN_VALUE);
        queue.add(0);

        assertEquals(Integer.MIN_VALUE, queue.poll());
        assertEquals(0, queue.poll());
        assertEquals(Integer.MAX_VALUE, queue.poll());
    }

    @Test
    @DisplayName("Wielokrotne add i poll")
    void testMultipleAddPoll() {
        for (int round = 0; round < 10; round++) {
            queue.add(round * 3);
            queue.add(round * 3 + 1);
            queue.add(round * 3 + 2);
            queue.poll();
        }

        assertEquals(20, queue.size());
    }

    @Test
    @DisplayName("add rzuca wyjątek dla null")
    void testAddNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> queue.add(null));
    }

    @Test
    @DisplayName("Resize przy dużej liczbie elementów")
    void testResizeTriggered() {
        // Dodaj więcej niż INITIAL_CAPACITY elementów
        for (int i = 100; i >= 0; i--) {
            queue.add(i);
        }

        assertEquals(101, queue.size());

        // Sprawdź że nadal działa min-heap property
        for (int i = 0; i <= 100; i++) {
            assertEquals(i, queue.poll());
        }
    }

    @Test
    @DisplayName("Sekwencja rosnąca")
    void testAscendingSequence() {
        for (int i = 1; i <= 10; i++) {
            queue.add(i);
        }

        for (int i = 1; i <= 10; i++) {
            assertEquals(i, queue.poll());
        }
    }

    @Test
    @DisplayName("Sekwencja malejąca")
    void testDescendingSequence() {
        for (int i = 10; i >= 1; i--) {
            queue.add(i);
        }

        for (int i = 1; i <= 10; i++) {
            assertEquals(i, queue.poll());
        }
    }
}
