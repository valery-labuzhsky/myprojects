package statref.model;

import statref.model.expression.SExpression;

public interface SInitializer extends SElement {
    SExpression getInitializer();

    SElement getVariable();
}
