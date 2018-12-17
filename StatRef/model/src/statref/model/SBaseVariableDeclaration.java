package statref.model;

import statref.model.expression.SExpression;
import statref.model.expression.SVariable;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SBaseVariableDeclaration extends SModifiers, SElement, SInitializer {
    SType getType();

    String getName();

    SVariable usage();

}
