package statref.model.builder.members;

import statref.model.types.SType;
import statref.model.SVariableDeclaration;
import statref.model.expressions.SExpression;

public class BVariableDeclaration extends BBaseVariableDeclaration<BVariableDeclaration> implements SVariableDeclaration {
    private SExpression expression;

    public BVariableDeclaration(SType type, String name) {
        super(type, name);
    }

    @Override
    public SExpression getInitializer() {
        return expression;
    }

    public BVariableDeclaration body(SExpression expression) {
        this.expression = expression;
        return this;
    }
}
