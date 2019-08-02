package statref.model.builder;

import statref.model.SFieldDeclaration;
import statref.model.SType;
import statref.model.expression.SExpression;

public class BFieldDeclaration extends BBaseVariableDeclaration<BFieldDeclaration> implements SFieldDeclaration {
    private SExpression expression;

    public BFieldDeclaration(SType type, String name) {
        super(type, name);
    }

    @Override
    public SExpression getInitializer() {
        return expression;
    }

    public BFieldDeclaration body(SExpression expression) {
        this.expression = expression;
        return (BFieldDeclaration) this;
    }
}
