package pl.edu.pw.ee.aisd2025zex5.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testy dla CustomHashMap.
 */
class CustomHashMapTest {

    private CustomHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new CustomHashMap<>();
    }

    @Test
    @DisplayName("put i get - podstawowe operacje")
    void testPutAndGet() {
        map.put("key1", 100);
        map.put("key2", 200);

        assertEquals(100, map.get("key1"));
        assertEquals(200, map.get("key2"));
    }

    @Test
    @DisplayName("get zwraca null dla nieistniejącego klucza")
    void testGetNonExistentKey() {
        assertNull(map.get("nonexistent"));
    }

    @Test
    @DisplayName("put nadpisuje istniejącą wartość")
    void testPutOverwritesValue() {
        map.put("key", 100);
        map.put("key", 200);

        assertEquals(200, map.get("key"));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("containsKey działa poprawnie")
    void testContainsKey() {
        map.put("exists", 1);

        assertTrue(map.containsKey("exists"));
        assertFalse(map.containsKey("notexists"));
    }

    @Test
    @DisplayName("size zwraca prawidłową liczbę elementów")
    void testSize() {
        assertEquals(0, map.size());

        map.put("a", 1);
        assertEquals(1, map.size());

        map.put("b", 2);
        assertEquals(2, map.size());

        // Nadpisanie nie zwiększa size
        map.put("a", 3);
        assertEquals(2, map.size());
    }

    @Test
    @DisplayName("entrySet zwraca wszystkie wpisy")
    void testEntrySet() {
        map.put("a", 1);
        map.put("b", 2);

        Entry<String, Integer>[] entries = map.entrySet();

        assertEquals(2, entries.length);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100, 500, 1000})
    @DisplayName("Obsługuje wiele elementów")
    void testManyElements(int count) {
        for (int i = 0; i < count; i++) {
            map.put("key" + i, i);
        }

        assertEquals(count, map.size());

        for (int i = 0; i < count; i++) {
            assertEquals(i, map.get("key" + i));
        }
    }

    @Test
    @DisplayName("Obsługuje kolizje hashCode")
    void testHashCollisions() {
        // Klucze które mogą mieć taki sam hash w niektórych implementacjach
        map.put("Aa", 1);
        map.put("BB", 2);

        assertEquals(1, map.get("Aa"));
        assertEquals(2, map.get("BB"));
    }

    @Test
    @DisplayName("put rzuca wyjątek dla null klucza")
    void testPutNullKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> map.put(null, 100));
    }

    @Test
    @DisplayName("get zwraca null dla null klucza")
    void testGetNullKey() {
        assertNull(map.get(null));
    }

    // Testy dla różnych typów kluczy
    @Test
    @DisplayName("Działa z Integer jako klucz")
    void testIntegerKey() {
        CustomHashMap<Integer, String> intMap = new CustomHashMap<>();

        intMap.put(1, "one");
        intMap.put(2, "two");
        intMap.put(3, "three");

        assertEquals("one", intMap.get(1));
        assertEquals("two", intMap.get(2));
        assertEquals("three", intMap.get(3));
    }

    @Test
    @DisplayName("Działa z Character jako klucz")
    void testCharacterKey() {
        CustomHashMap<Character, Integer> charMap = new CustomHashMap<>();

        charMap.put('A', 65);
        charMap.put('B', 66);
        charMap.put('Z', 90);

        assertEquals(65, charMap.get('A'));
        assertEquals(66, charMap.get('B'));
        assertEquals(90, charMap.get('Z'));
    }

    @Test
    @DisplayName("Iteracja przez entrySet")
    void testIterateEntrySet() {
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        int sum = 0;
        for (Entry<String, Integer> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            sum += entry.getValue();
        }

        assertEquals(6, sum);
    }

    @Test
    @DisplayName("Wielobajtowe klucze String")
    void testMultiByteStringKeys() {
        map.put("ABC", 1);
        map.put("ABCDEF", 2);
        map.put("XYZ", 3);

        assertEquals(1, map.get("ABC"));
        assertEquals(2, map.get("ABCDEF"));
        assertEquals(3, map.get("XYZ"));
    }

    @Test
    @DisplayName("Resize przy dużej liczbie elementów")
    void testResizeTriggered() {
        // Dodaj więcej niż INITIAL_CAPACITY * LOAD_FACTOR elementów
        for (int i = 0; i < 100; i++) {
            map.put("key" + i, i);
        }

        assertEquals(100, map.size());

        // Wszystkie elementy są nadal dostępne
        for (int i = 0; i < 100; i++) {
            assertEquals(i, map.get("key" + i));
        }
    }
}
