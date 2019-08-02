package statref.model.builder;

import statref.model.*;
import statref.model.expression.SExpression;

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
