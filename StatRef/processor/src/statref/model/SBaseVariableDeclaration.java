package statref.model;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SBaseVariableDeclaration extends SModifiers {
    SType getType();

    String getName();

    SExpression getExpression();

    SVariable usage();

}
