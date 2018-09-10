package statref.model.reflect;

import statref.model.*;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class RClass implements SClass {
    private final Class clazz;

    public RClass(Class clazz) {
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

    @Override
    public Collection<SMethod> getMethods() {
        return null; // TODO implement
    }

    @Override
    public List<SVariable> getFields() {
        return null; // TODO
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null; // TODO
    }

    @Override
    public SType getGenericType() {
        return null; // TODO
    }

    public String toString() {
        return getSimpleName();
    }

}