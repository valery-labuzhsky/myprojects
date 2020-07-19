package statref.model.builder.expressions;

import statref.model.expressions.SLocalVariable;
import statref.model.members.SVariableDeclaration;

public class BLocalVariable extends BExpression implements SLocalVariable {
    private final String name;

    public BLocalVariable(String name) {
        this.name = name;
    }

    public BLocalVariable(SVariableDeclaration declaration) {
        this(declaration.getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
