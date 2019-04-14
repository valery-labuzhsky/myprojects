package statref.model.builder;

import statref.model.SBaseVariableDeclaration;
import statref.model.SType;
import statref.model.expression.SExpression;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;

public abstract class BBaseVariableDeclaration<B extends BBaseVariableDeclaration> extends BElement implements SBaseVariableDeclaration, BModifiers<B> {
    private final SType type;
    private final String name;
    private SExpression expression;
    private ArrayList<Modifier> modifiers = new ArrayList<>();

    public BBaseVariableDeclaration(SType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public SType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SExpression getInitializer() {
        return expression;
    }

    @Override
    public BVariable usage() {
        return new BVariable(name);
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }

    public B body(SExpression expression) {
        this.expression = expression;
        return (B) this;
    }
}
