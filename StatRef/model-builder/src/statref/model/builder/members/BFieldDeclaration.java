package statref.model.builder.members;

import statref.model.expressions.SExpression;
import statref.model.members.SFieldDeclaration;
import statref.model.types.SType;

public class BFieldDeclaration extends BVariableDeclaration<BFieldDeclaration> implements SFieldDeclaration {
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
