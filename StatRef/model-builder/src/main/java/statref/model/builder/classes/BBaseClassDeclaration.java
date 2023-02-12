package statref.model.builder.classes;

import statref.model.classes.SBaseClassDeclaration;
import statref.model.members.SClassMemeber;
import statref.model.builder.BElement;

import java.util.ArrayList;
import java.util.List;

public abstract class BBaseClassDeclaration<T extends BBaseClassDeclaration> extends BElement implements SBaseClassDeclaration {
    private final ArrayList<SClassMemeber> methods = new ArrayList<>();

    public BBaseClassDeclaration() {
    }

    @Override
    public List<SClassMemeber> getMembers() {
        return methods;
    }

    public T member(SClassMemeber method) {
        methods.add(method);
        return (T) this;
    }
}
