package statref.api;

/**
 * Created on 17/12/17.
 *
 * @author ptasha
 */
public interface Getter<O, V> {
    V get(O object);
}
