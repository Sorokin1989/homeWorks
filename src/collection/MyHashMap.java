package collection;


import java.util.Objects;

//Необходимо написать собственную реализацию HashMap. Обязательные методы: get, put, remove.
public class MyHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private Node<K, V>[] table;

    private int size;
    private final double loadFactor;
    private boolean resizing = false;


    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity, double loadFactor) {
        this.table = (Node<K, V>[]) new Node[capacity];
        this.loadFactor = loadFactor;
        size = 0;
    }

    public void put(K key, V value) {
        reSize();
        int index = getIndex(key, table.length);

        Node<K, V> element = table[index];

        while (element != null) {

            if (Objects.equals(element.key, key)) {
                element.value = value;
                return;
            } else {
                element = element.next;
            }
        }
        table[index] = new Node<>(key, value, table[index]);
        size++;

    }

    private int getIndex(K key, int capacity) {
        if (key == null) {
            return 0;
        }
        return (key.hashCode() & 0x7fffffff) % capacity;
    }


    public V get(K key) {

        int index = getIndex(key, table.length);
        Node<K, V> element = table[index];

        while (element != null) {
            if (Objects.equals(element.key, key)) {
                return element.value;
            }
            element = element.next;
        }

        return null;
    }

    public V remove(K key) {
        int index = getIndex(key, table.length);
        Node<K, V> element = table[index];
        Node<K, V> prev = null;
        while (element != null) {
            if (Objects.equals(element.key, key)) {

                if (prev != null) {
                    prev.next = element.next;
                    size--;
                    return element.value;
                }
                table[index] = element.next;
                size--;
                return element.value;


            }
            prev = element;
            element = element.next;
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return table.length;
    }

    private void reSize() {
        if (resizing) return;
        if ((double) size / table.length >= loadFactor) {
            Node<K, V>[] newTable = (Node<K, V>[]) new Node[table.length * 2];
            size = 0;
            resizing = true;
            Node<K, V>[] oldTable = table;
            table = newTable;

            int index = 0;
            while (index < oldTable.length) {
                Node<K, V> element = oldTable[index];
                while (element != null) {
                    Node<K, V> next = element.next;

                    put(element.key, element.value);

                    element = next;

                }
                index++;
            }
            resizing = false;

        }
    }


    private static class Node<K, V> {
        public final K key;
        public V value;
        public Node<K, V> next;

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }


    }

}
