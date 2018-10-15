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
public class RClassDeclaration extends RClassRef implements SClassDeclaration {

    public RClassDeclaration(Class clazz) {
        super(clazz);
    }

    @Override
    public List<SClassMemeber> getMembers() {
        return null; // TODO implement
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null; // TODO
    }

    @Override
    public SClass usage() {
        return new RClass(clazz);
    }

    @Override
    public SClass getExtends() {
        return null; // TODO
    }

    @Override
    public List<SGenericDeclaration> getGenerics() {
        throw new UnsupportedOperationException("Not yet implemented"); // TODO
    }

    @Override
    public SClass usage(List<SType> generics) {
        throw new UnsupportedOperationException("Not yet implemented"); // TODO
    }
}