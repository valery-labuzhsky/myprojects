package statref.model.reflect;

import statref.model.SClassRef;
import statref.model.SPackage;

public class RClassRef implements SClassRef {
    protected final Class clazz;

    public RClassRef(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public SPackage getPackage() {
        return null; // TODO implement
    }

    @Override
    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    public String toString() {
        return getSimpleName();
    }
}
