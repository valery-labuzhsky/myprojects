package tools.bindiff.utils;

import java.util.TreeSet;

/**
 * Created on 21/02/15.
 *
 * @author ptasha
 */
public class TreeSetFactory<V> implements CollectionFactory<V, TreeSet<V>> {
    @Override
    public TreeSet<V> create() {
        return new TreeSet<>();
    }
}
