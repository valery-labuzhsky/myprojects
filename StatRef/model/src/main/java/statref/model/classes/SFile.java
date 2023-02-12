package statref.model.classes;

import statref.model.types.SClass;

import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SFile {
    SPackage getPackage();

    List<SClass> getImports(); // TODO it's broader than that

    List<SClassDeclaration> getClasses();
}
