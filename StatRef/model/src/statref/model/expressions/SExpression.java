package statref.model.expressions;

import statref.model.SElement;
import statref.model.statements.SStatement;
import statref.model.types.SType;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public interface SExpression extends SElement {
    default SType getType() {
        return null;
    }

    default boolean isStatement() {
        return false;
    }

    default SStatement toStatement() {
        throw new UnsupportedOperationException(getClass().getName());
    }
}
