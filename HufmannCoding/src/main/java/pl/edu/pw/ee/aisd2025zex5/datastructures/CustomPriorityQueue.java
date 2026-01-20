package pl.edu.pw.ee.aisd2025zex5.datastructures;

/**
 * Kolejka priorytetowa oparta na kopcu minimalnym (Min-Heap).
 * 
 * Kopiec to drzewo binarne, gdzie rodzic jest MNIEJSZY od dzieci.
 * Dzięki temu najmniejszy element jest zawsze na szczycie (indeks 0).
 * 
 * Reprezentacja tablicowa:
 * - Dla węzła na pozycji i:
 *   - Lewe dziecko: 2*i + 1
 *   - Prawe dziecko: 2*i + 2
 *   - Rodzic: (i - 1) / 2
 * 
 * Przykład kopca (częstości węzłów Huffmana):
 * 
 *        1           Tablica: [1, 3, 2, 7, 5]
 *       / \
 *      3   2         Indeksy:  0  1  2  3  4
 *     / \
 *    7   5
 * 
 * @param <T> typ elementów, musi implementować Comparable
 */
public class CustomPriorityQueue<T extends Comparable<T>> {

    private static final int INITIAL_CAPACITY = 16;
    
    private Object[] heap;  // Tablica przechowująca elementy
    private int size;       // Aktualna liczba elementów

    public CustomPriorityQueue() {
        this.heap = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Dodaje element do kolejki.
     * 
     * 1. Wstaw na koniec tablicy
     * 2. "Wypływaj" w górę (siftUp) - zamieniaj z rodzicem dopóki rodzic > dziecko
     * 
     * Złożoność: O(log n)
     */
    public void add(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Element nie może być null");
        }
        
        // Powiększ tablicę jeśli pełna
        if (size >= heap.length) {
            resize();
        }
        
        // Wstaw na koniec
        heap[size] = element;
        
        // Napraw kopiec w górę
        siftUp(size);
        
        size++;
    }

    /**
     * Pobiera i usuwa najmniejszy element (korzeń kopca).
     * 
     * 1. Zapamiętaj korzeń (indeks 0)
     * 2. Przenieś ostatni element na miejsce korzenia
     * 3. "Zatapiaj" w dół (heapify) - zamieniaj z mniejszym dzieckiem
     * 
     * Złożoność: O(log n)
     * 
     * @return najmniejszy element lub null jeśli kolejka pusta
     */
    @SuppressWarnings("unchecked")
    public T poll() {
        if (size == 0) {
            return null;
        }
        
        // Zapamiętaj korzeń (najmniejszy element)
        T root = (T) heap[0];
        
        // Przenieś ostatni element na szczyt
        size--;
        heap[0] = heap[size];
        heap[size] = null;  // Pomoc dla GC
        
        // Napraw kopiec w dół (jeśli coś zostało)
        if (size > 0) {
            heapify(0);
        }
        
        return root;
    }

    /**
     * Zwraca liczbę elementów w kolejce.
     */
    public int size() {
        return size;
    }

    /**
     * Sprawdza czy kolejka jest pusta.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * "Wypływanie" w górę - przywraca własność kopca po dodaniu elementu.
     * 
     * Porównuje element z rodzicem i zamienia jeśli element < rodzic.
     * Powtarza aż dotrze do korzenia lub znajdzie właściwe miejsce.
     */
    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            
            T current = (T) heap[index];
            T parent = (T) heap[parentIndex];
            
            // Jeśli rodzic <= dziecko, kopiec jest OK
            if (parent.compareTo(current) <= 0) {
                break;
            }
            
            // Zamień miejscami
            heap[index] = parent;
            heap[parentIndex] = current;
            
            // Kontynuuj w górę
            index = parentIndex;
        }
    }

    /**
     * "Zatapianie" w dół (heapify) - przywraca własność kopca po usunięciu korzenia.
     * 
     * Porównuje węzeł z dziećmi i zamienia z MNIEJSZYM dzieckiem.
     * Powtarza aż dotrze do liścia lub znajdzie właściwe miejsce.
     */
    @SuppressWarnings("unchecked")
    private void heapify(int index) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;
            
            // Sprawdź czy lewe dziecko istnieje i jest mniejsze
            if (leftChild < size) {
                T left = (T) heap[leftChild];
                T current = (T) heap[smallest];
                if (left.compareTo(current) < 0) {
                    smallest = leftChild;
                }
            }
            
            // Sprawdź czy prawe dziecko istnieje i jest jeszcze mniejsze
            if (rightChild < size) {
                T right = (T) heap[rightChild];
                T currentSmallest = (T) heap[smallest];
                if (right.compareTo(currentSmallest) < 0) {
                    smallest = rightChild;
                }
            }
            
            // Jeśli rodzic jest najmniejszy, koniec
            if (smallest == index) {
                break;
            }
            
            // Zamień z mniejszym dzieckiem
            Object temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;
            
            // Kontynuuj w dół
            index = smallest;
        }
    }

    /**
     * Podwaja rozmiar tablicy gdy jest pełna.
     */
    private void resize() {
        Object[] newHeap = new Object[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }
}
