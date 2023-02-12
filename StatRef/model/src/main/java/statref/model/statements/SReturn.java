package statref.model.statements;

import statref.model.expressions.SExpression;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public interface SReturn extends SStatement {
    SExpression getExpression();
}
