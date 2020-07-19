package statref.model.builder.members;

import statref.model.SLocalVariableDeclaration;
import statref.model.expressions.SExpression;
import statref.model.types.SType;

public class BLocalVariableDeclaration extends BVariableDeclaration<BLocalVariableDeclaration> implements SLocalVariableDeclaration {
    private SExpression expression;

    public BLocalVariableDeclaration(SType type, String name) {
        super(type, name);
    }

    @Override
    public SExpression getInitializer() {
        return expression;
    }

    public BLocalVariableDeclaration body(SExpression expression) {
        this.expression = expression;
        return this;
    }
}
