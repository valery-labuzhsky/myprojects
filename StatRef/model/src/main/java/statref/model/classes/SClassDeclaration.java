package statref.model.classes;

import statref.model.SGenericDeclaration;
import statref.model.SModifiers;
import statref.model.types.SClass;
import statref.model.types.SType;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SClassDeclaration extends SModifiers, SBaseClassDeclaration {
    default SPackage getPackage() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default String getSimpleName() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default SClass getExtends() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default List<SGenericDeclaration> getGenerics() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    default SClass usage() {
        return new SClass(this.getSimpleName());
    }

    default SClass usage(List<SType> generics) {
        return new SClass(getSimpleName(), generics);
    }
}
