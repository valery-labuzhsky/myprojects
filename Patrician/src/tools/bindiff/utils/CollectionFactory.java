package tools.bindiff.utils;

import java.util.Collection;

/**
 * Created on 21/02/15.
 *
 * @author ptasha
 */
public interface CollectionFactory<V, C extends Collection<V>> {
    C create();
}
