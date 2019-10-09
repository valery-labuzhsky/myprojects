package statref.model.builder.statements;

import statref.model.statements.SMethodStatement;
import statref.model.builder.BElement;
import statref.model.expressions.SMethod;

public class BMethodStatement extends BElement implements SMethodStatement {
    private final SMethod method;

    public BMethodStatement(SMethod method) {
        this.method = method;
    }

    @Override
    public SMethod getMethod() {
        return method;
    }
}
