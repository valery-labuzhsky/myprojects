package statref.model.builder.expressions;

import statref.model.members.SBaseVariableDeclaration;
import statref.model.expressions.SVariable;

public class BVariable extends BExpression implements SVariable {
    private final String name;

    public BVariable(String name) {
        this.name = name;
    }

    public BVariable(SBaseVariableDeclaration declaration) {
        this(declaration.getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
