package statref.model.builder;

import statref.model.*;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BReturn implements SReturn {
    private final SExpression expression;

    public BReturn(SExpression expression) {
        this.expression = expression;
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }
}
