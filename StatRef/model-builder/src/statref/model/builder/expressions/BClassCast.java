package statref.model.builder.expressions;

import statref.model.expressions.SClassCast;
import statref.model.expressions.SExpression;
import statref.model.types.SType;

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
