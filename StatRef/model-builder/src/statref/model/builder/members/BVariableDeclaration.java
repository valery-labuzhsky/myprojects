package statref.model.builder.members;

import statref.model.builder.BElement;
import statref.model.builder.BModifiers;
import statref.model.builder.expressions.BLocalVariable;
import statref.model.members.SVariableDeclaration;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;

public abstract class BVariableDeclaration<B extends BVariableDeclaration> extends BElement implements SVariableDeclaration, BModifiers<B> {
    private final SType type;
    private final String name;
    private ArrayList<Modifier> modifiers = new ArrayList<>();

    public BVariableDeclaration(SType type, String name) {
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

    public BLocalVariable usage() {
        return new BLocalVariable(name);
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }

}
