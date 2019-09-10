package statref.model;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SType { // TODO I need one class, just different constructors
    default boolean isPrimitive(Class<?> primitive) {
        if (this instanceof SPrimitive) {
            return ((SPrimitive) this).getJavaClass()== primitive;
        } else {
            return false;
        }
    }

    default SType getGenericType() {
        return this;
    }
}
