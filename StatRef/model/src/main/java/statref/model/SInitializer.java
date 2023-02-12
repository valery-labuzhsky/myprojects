package statref.model;

import statref.model.expressions.SExpression;

public interface SInitializer extends SElement {
    SExpression getInitializer();
}
