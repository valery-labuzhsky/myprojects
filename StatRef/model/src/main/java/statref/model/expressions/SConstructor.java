package statref.model.expressions;

import statref.model.types.SClass;

import java.util.List;

public interface SConstructor extends SExpression {
    SClass getSClass();

    List<SLocalVariable> getParameters();
}
