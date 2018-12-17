package statref.model.builder;

import statref.model.expression.SClassCast;
import statref.model.expression.SExpression;
import statref.model.SType;

public class BClassCast extends BExpression implements SClassCast {
    private final SExpression expression;
    private final SType type;

    public BClassCast(SExpression expression, SType type) {
        this.expression = expression;
        this.type = type;
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }

    @Override
    public SType getType() {
        return type;
    }
}
