package patrician.save.objects;

import java.util.List;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public interface Item<T> extends Cloneable {
    int size();
    int read(int offset, List<Integer> data);
    int get(int offset);
    String diff(T item);
    T clone();
}
