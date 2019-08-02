package statref.model;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SBaseVariableDeclaration extends SModifiers, SElement {
    SType getType();

    String getName();
}
