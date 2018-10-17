package statref.model.mirror;

import statref.model.SClass;
import statref.model.SType;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;

public class MClass extends MBase<DeclaredType> implements SClass {
    private final TypeElement type;
    private final List<SType> generics = new ArrayList<>();

    public MClass(DeclaredType mirror) {
        super(mirror);
        type = (TypeElement) mirror.asElement();
    }

    public MClass(DeclaredType mirror, List<SType> generics) {
        this(mirror);
        this.generics.addAll(generics);
    }

    @Override
    public List<SType> getGenerics() {
        return generics;
    }

    @Override
    public String getName() {
        return type.getSimpleName().toString();
    }

    public MPackage getPackage() {
        return new MPackage(type.getEnclosingElement());
    }
}
