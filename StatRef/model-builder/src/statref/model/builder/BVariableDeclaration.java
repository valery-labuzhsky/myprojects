package statref.model.builder;

import statref.model.SType;
import statref.model.SVariableDeclaration;
import statref.model.expression.SExpression;

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
