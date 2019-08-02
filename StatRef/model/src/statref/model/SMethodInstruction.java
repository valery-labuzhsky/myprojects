package statref.model;

import statref.model.expression.SMethod;

public interface SMethodInstruction extends SStatement {
    SMethod getMethod();
}
