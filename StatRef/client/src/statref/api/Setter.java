package statref.api;

/**
 * Created on 17/12/17.
 *
 * @author ptasha
 */
public interface Setter<O, V> {
    void set(O object, V value);
}
