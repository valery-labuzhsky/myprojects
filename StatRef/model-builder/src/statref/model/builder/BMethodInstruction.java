package statref.model.builder;

import statref.model.SMethodInstruction;
import statref.model.expression.SMethod;

public class BMethodInstruction implements SMethodInstruction {
    private final SMethod method;

    public BMethodInstruction(SMethod method) {
        this.method = method;
    }

    @Override
    public SMethod getMethod() {
        return method;
    }
}
