package statref.model;

import statref.model.expression.SExpression;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public interface SReturn extends SInstruction {
    SExpression getExpression();
}
