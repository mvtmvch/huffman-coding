package pl.edu.pw.ee.aisd2025zex5.datastructures;

/**
 * Mapa haszująca z metodą łańcuchową (chain hashing).
 * 
 * Struktura:
 * - Tablica kubełków (buckets)
 * - Każdy kubełek to początek listy jednokierunkowej
 * - Kolizje rozwiązujemy dodając do listy
 * 
 * Przykład dla 4 kubełków:
 * 
 *   Indeks │ Lista
 *   ───────┼──────────────────────
 *      0   │ ("AA", 5) -> ("EE", 2) -> null
 *      1   │ null
 *      2   │ ("BB", 3) -> null
 *      3   │ ("CC", 1) -> ("DD", 4) -> null
 * 
 * @param <K> typ klucza
 * @param <V> typ wartości
 */
public class CustomHashMap<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] buckets;  // Tablica kubełków
    private int size;              // Liczba par klucz-wartość

    /**
     * Węzeł listy jednokierunkowej w kubełku.
     */
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;  // Następny węzeł w łańcuchu

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.buckets = new Node[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Wstawia lub aktualizuje parę klucz-wartość.
     * 
     * 1. Oblicz hash klucza → indeks kubełka
     * 2. Przeszukaj listę w kubełku
     * 3. Jeśli klucz istnieje → aktualizuj wartość
     * 4. Jeśli nie → dodaj na początek listy
     * 
     * Złożoność: O(1) średnio, O(n) pesymistycznie
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Klucz nie może być null");
        }

        // Powiększ tablicę jeśli za dużo elementów
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(key);
        
        // Szukaj klucza w łańcuchu
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                // Klucz istnieje - aktualizuj wartość
                current.value = value;
                return;
            }
            current = current.next;
        }

        // Klucz nie istnieje - dodaj na początek łańcucha
        buckets[index] = new Node<>(key, value, buckets[index]);
        size++;
    }

    /**
     * Pobiera wartość dla klucza.
     * 
     * @return wartość lub null jeśli klucz nie istnieje
     */
    public V get(K key) {
        if (key == null) {
            return null;
        }

        int index = getIndex(key);
        
        // Szukaj w łańcuchu
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;  // Nie znaleziono
    }

    /**
     * Sprawdza czy mapa zawiera klucz.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Zwraca liczbę par klucz-wartość.
     */
    public int size() {
        return size;
    }

    /**
     * Zwraca tablicę wszystkich wpisów.
     * Używane do iteracji po mapie.
     */
    @SuppressWarnings("unchecked")
    public Entry<K, V>[] entrySet() {
        Entry<K, V>[] entries = new Entry[size];
        int i = 0;

        // Przejdź przez wszystkie kubełki
        for (Node<K, V> bucket : buckets) {
            // Przejdź przez łańcuch w kubełku
            Node<K, V> current = bucket;
            while (current != null) {
                entries[i++] = new Entry<>(current.key, current.value);
                current = current.next;
            }
        }

        return entries;
    }

    /**
     * Oblicza indeks kubełka dla klucza.
     * 
     * & 0x7FFFFFFF - zeruje bit znaku, zapewnia dodatni wynik
     * % buckets.length - mapuje na zakres [0, length-1]
     */
    private int getIndex(K key) {
        int hash = key.hashCode() & 0x7FFFFFFF;  // Usuń bit znaku
        return hash % buckets.length;
    }

    /**
     * Podwaja rozmiar tablicy i przenosi wszystkie elementy.
     * 
     * Konieczne, bo indeks zależy od rozmiaru tablicy!
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        size = 0;

        // Przenieś wszystkie elementy (rehashing)
        for (Node<K, V> bucket : oldBuckets) {
            Node<K, V> current = bucket;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}
