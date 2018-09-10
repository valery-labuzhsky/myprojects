package statref.model;

import java.util.Collection;
import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SClass extends SType, SModifiers {
    SPackage getPackage();

    String getSimpleName();

    Collection<SMethod> getMethods();

    List<SVariable> getFields();
}
