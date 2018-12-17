package statref.model.expression;

import statref.model.expression.SExpression;

public interface SArrayItem extends SExpression {
    SExpression getExpression();

    SExpression getIndex();
}
