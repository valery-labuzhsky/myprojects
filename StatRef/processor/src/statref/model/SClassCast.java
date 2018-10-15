package statref.model;

import statref.model.SExpression;
import statref.model.SType;

public interface SClassCast extends SExpression {
    SExpression getExpression();

    SType getType();
}
