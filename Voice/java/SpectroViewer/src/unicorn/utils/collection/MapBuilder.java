package unicorn.utils.collection;

import java.util.Map;

/**
 * @author unicorn
 */
public class MapBuilder<K, V> {
    private Map<K, V> map;

    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> getMap() {
        return map;
    }
}
