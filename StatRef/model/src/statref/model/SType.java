package statref.model;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SType {
    default SType getGenericType() {
        return this;
    }
}
