package statref.model.builder;

import statref.model.expression.SVariable;

public class BVariable extends BExpression implements SVariable {
    private final String name;

    public BVariable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
