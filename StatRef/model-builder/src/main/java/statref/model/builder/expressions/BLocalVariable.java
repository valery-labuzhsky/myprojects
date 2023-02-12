package statref.model.builder.expressions;

import statref.model.expressions.SLocalVariable;
import statref.model.members.SVariableDeclaration;

public class BLocalVariable extends BReference implements SLocalVariable {

    public BLocalVariable(String name) {
        super(name);
    }

    public BLocalVariable(SVariableDeclaration declaration) {
        this(declaration.getName());
    }

    public BLocalVariable(SLocalVariable reference) {
        super(reference);
    }
}
