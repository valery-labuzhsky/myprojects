package statref.model.builder.statements;

import statref.model.builder.BElement;
import statref.model.expressions.SExpression;
import statref.model.statements.SReturn;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BReturn extends BElement implements SReturn {
    private final SExpression expression;

    public BReturn(SExpression expression) {
        this.expression = expression;
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }
}
