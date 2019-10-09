package statref.model.classes;

import statref.model.SGenericDeclaration;
import statref.model.SModifiers;
import statref.model.types.SType;
import statref.model.types.SClass;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SClassDeclaration extends SModifiers, SBaseClassDeclaration {
    SPackage getPackage();

    String getSimpleName();

    SClass getExtends();

    List<SGenericDeclaration> getGenerics();

    SClass usage(List<SType> generics);
}
