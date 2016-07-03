package tools.bindiff.utils;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created on 21/02/15.
 *
 * @author ptasha
 */
public class CollectionHashMap<K, V, C extends Collection<V>> extends HashMap<K, C> {
    private final CollectionFactory<V, C> factory;

    public CollectionHashMap(CollectionFactory<V, C> factory) {
        this.factory = factory;
    }

    public boolean add(K key, V value) {
        C collection = this.get(key);
        if (collection==null) {
            return newKey(key, value);
        } else {
            return oldKey(value, collection);
        }
    }

    protected boolean oldKey(V value, C collection) {
        return add(collection, value);
    }

    protected boolean newKey(K key, V value) {
        C collection;
        collection = factory.create();
        this.put(key, collection);
        return add(collection, value);
    }

    private boolean add(C collection, V value) {
        return collection.add(value);
    }
}
