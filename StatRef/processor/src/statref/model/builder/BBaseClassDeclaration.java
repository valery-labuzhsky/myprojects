package statref.model.builder;

import statref.model.SBaseClassDeclaration;
import statref.model.SClassMemeber;
import statref.model.SMethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public abstract class BBaseClassDeclaration<T extends BBaseClassDeclaration> implements SBaseClassDeclaration {
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
