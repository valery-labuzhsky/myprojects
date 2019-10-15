package statref.model.types;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public class SType {
    public boolean isClass(Class<?> clazz) {
        if (this instanceof SClass) {
            return ((SClass) this).getJavaClass() == clazz;
        } else {
            return false;
        }
    }

    public SType getGenericType() {
        return this;
    }
}
