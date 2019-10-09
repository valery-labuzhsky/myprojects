package statref.model.expressions;

import statref.model.types.SType;

public interface SClassCast extends SExpression {
    SExpression getExpression();

    SType getType();
}
