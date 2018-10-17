package statref.model;

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
