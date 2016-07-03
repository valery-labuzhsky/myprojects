package tools.bindiff.simple;

import java.util.Iterator;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public interface Sequence extends Iterator<Integer> {
    void skip(int size);
    void close();
}
