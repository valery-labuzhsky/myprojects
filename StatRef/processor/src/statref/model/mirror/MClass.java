package statref.model.mirror;

import statref.model.SClass;
import statref.model.SGeneric;
import statref.model.SType;

import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;

public class MClass extends MClassRef implements SClass {
    private final List<SType> generics = new ArrayList<>();

    public MClass(DeclaredType mirror) {
        super(mirror);
    }

    public MClass(DeclaredType mirror, List<SType> generics) {
        super(mirror);
        this.generics.addAll(generics);
    }

    @Override
    public List<SType> getGenerics() {
        return generics;
    }
}
