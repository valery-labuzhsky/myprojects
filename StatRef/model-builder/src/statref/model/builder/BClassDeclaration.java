package statref.model.builder;

import statref.model.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BClassDeclaration extends BBaseClassDeclaration<BClassDeclaration> implements SClassDeclaration, BModifiers<BClassDeclaration> {
    private SPackage package_;
    private SClass parent;
    private final String name;
    private final ArrayList<Modifier> modifiers = new ArrayList<>();
    private final ArrayList<SGenericDeclaration> parameters = new ArrayList<>();

    public BClassDeclaration(String name) {
        this.name = name;
    }

    @Override
    public SClass usage() {
        return new BClass(this);
    }

    @Override
    public SClass usage(List<SType> generics) {
        return new BClass(this, generics);
    }

    @Override
    public SPackage getPackage() {
        return package_;
    }

    public BClassDeclaration package_(SPackage package_) { // TODO I'm fully described with parent either class of file
        this.package_ = package_;
        return this;
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    @Override
    public SClass getExtends() {
        return parent;
    }

    @Override
    public List<SGenericDeclaration> getGenerics() {
        return parameters;
    }

    public BClassDeclaration parameter(SGenericDeclaration param) {
        parameters.add(param);
        return this;
    }

    public BClassDeclaration parameters(List<SGenericDeclaration> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public BClassDeclaration extends_(SClass parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return modifiers;
    }
}
