package menu;

/**
 * A class that contains a simple key-value pair.
 *
 * @param <K> The type of a key object.
 * @param <V> The type of a value object.
 */
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + key + " = " + value + ")";
    }
}
