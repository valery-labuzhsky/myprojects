package statref.model.expressions;

import statref.model.SElement;
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
}
