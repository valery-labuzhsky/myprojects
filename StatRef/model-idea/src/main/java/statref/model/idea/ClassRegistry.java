package statref.model.idea;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassRegistry<K, V> {
    private final HashMap<Class<K>, V> registry = new LinkedHashMap<>();

    public void register(Class<K> key, V value) {
        registry.put(key, value);
    }

    @Nullable
    public V get(Class<K> clazz) {
        V value = registry.get(clazz);
        if (value == null) {
            for (Map.Entry<Class<K>, V> entry : registry.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    value = entry.getValue();
                    registry.put(clazz, value);
                    break;
                }
            }
        }
        return value;
    }

}
