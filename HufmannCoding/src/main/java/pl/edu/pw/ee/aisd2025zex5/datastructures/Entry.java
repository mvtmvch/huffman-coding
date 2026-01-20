package pl.edu.pw.ee.aisd2025zex5.datastructures;

/**
 * Para klucz-wartość używana w CustomHashMap.
 * 
 * @param <K> typ klucza
 * @param <V> typ wartości
 */
public class Entry<K, V> {

    private final K key;   // Klucz jest niemutowalny (final)
    private V value;       // Wartość można zmieniać (np. aktualizacja częstości)

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
