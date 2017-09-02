package unicorn.utils.collection;

import unicorn.utils.reflection.Reflection;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author unicorn
 */
public class CollectionMap<T, C extends Collection<V>, V> extends HashMap<T, C> {
    private final Class<? extends C> type;

    /**
     * Some shit happens with Idea and Java if correct generic is used.
     *
     * @param type type of collection. Should be C extends Collection<V>!
     */
    @SuppressWarnings("unchecked")
    public CollectionMap(Class<? extends Collection> type) {
        this.type = (Class<? extends C>) type;
    }

    public void add(T key, V value) {
        C items = get(key);
        if (items==null) {
            items = create();
            put(key, items);
        }
        items.add(value);
    }

    @Override
    public C get(Object key) {
        C items = super.get(key);
        if (items==null) return create();
        return items;
    }

    private C create() {
        return Reflection.create(type);
    }
}
