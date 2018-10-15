package statref.model.builder;

import statref.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BClass implements SClass {
    private final SClassRef classRef;
    private final ArrayList<SType> generics = new ArrayList<>();

    public BClass(SClassRef classRef, List<SType> generics) {
        this.classRef = classRef;
        this.generics.addAll(generics);
    }

    public BClass(SClassRef classRef, SType... generics) {
        this(classRef, Arrays.asList(generics));
    }

    public BClass(SClassDeclaration clazz) {
        this(new BClassRef(clazz));
    }

    public BClass(BClassDeclaration declaration, List<SType> generics) {
        this(new BClassRef(declaration), generics);
    }

    @Override
    public SPackage getPackage() {
        return classRef.getPackage();
    }

    @Override
    public String getSimpleName() {
        return classRef.getSimpleName();
    }

    @Override
    public List<SType> getGenerics() {
        return generics;
    }
}
