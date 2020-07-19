package statref.model.members;

import statref.model.SElement;
import statref.model.SModifiers;
import statref.model.types.SType;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SVariableDeclaration extends SModifiers, SElement {
    SType getType();

    String getName();
}
