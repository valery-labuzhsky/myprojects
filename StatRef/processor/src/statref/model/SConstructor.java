package statref.model;

import java.util.List;

public interface SConstructor extends SExpression {
    SClass getSClass();

    List<SVariable> getParameters();
}
