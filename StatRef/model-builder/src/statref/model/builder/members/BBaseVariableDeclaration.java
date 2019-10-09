package statref.model.builder.members;

import statref.model.members.SBaseVariableDeclaration;
import statref.model.types.SType;
import statref.model.builder.BElement;
import statref.model.builder.BModifiers;
import statref.model.builder.expressions.BVariable;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;

public abstract class BBaseVariableDeclaration<B extends BBaseVariableDeclaration> extends BElement implements SBaseVariableDeclaration, BModifiers<B> {
    private final SType type;
    private final String name;
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

    public BVariable usage() {
        return new BVariable(name);
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }

}
