package statref.model;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SType { // TODO I need one class, just different constructors
    default boolean isClass(Class<?> clazz) {
        if (this instanceof SClass) {
            return ((SClass) this).getJavaClass() == clazz;
        } else {
            return false;
        }
    }

    default SType getGenericType() {
        return this;
    }
}
