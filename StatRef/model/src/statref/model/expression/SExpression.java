package statref.model.expression;

import statref.model.SElement;
import statref.model.SType;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public interface SExpression extends SElement {
    default SType getType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
