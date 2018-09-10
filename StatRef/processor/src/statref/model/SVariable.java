package statref.model;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SVariable extends SModifiers {
    SType getType();

    SExpression getExpression();
}
