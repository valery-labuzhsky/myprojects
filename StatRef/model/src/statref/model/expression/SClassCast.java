package statref.model.expression;

import statref.model.SType;

public interface SClassCast extends SExpression {
    SExpression getExpression();

    SType getType();
}
