package statref.model.builder;

import statref.model.SExpression;

public interface SArrayItem extends SExpression {
    SExpression getExpression();

    SExpression getIndex();
}
