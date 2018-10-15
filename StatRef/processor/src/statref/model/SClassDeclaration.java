package statref.model;

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
