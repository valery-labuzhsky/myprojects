package statref.model.expression;

import statref.model.SClass;
import statref.model.expression.SExpression;
import statref.model.expression.SVariable;

import java.util.List;

public interface SConstructor extends SExpression {
    SClass getSClass();

    List<SVariable> getParameters();
}
