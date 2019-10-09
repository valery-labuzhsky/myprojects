package statref.model.builder.types;

import statref.model.types.SClass;
import statref.model.classes.SClassDeclaration;
import statref.model.types.SType;
import statref.model.builder.classes.BClassDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BClass implements SClass {
    private final String name;
    private final ArrayList<SType> generics = new ArrayList<>();

    public BClass(String name, List<SType> generics) {
        this.name = name;
        this.generics.addAll(generics);
    }

    public BClass(String name, SType... generics) {
        this(name, Arrays.asList(generics));
    }

    public BClass(SClassDeclaration clazz) {
        this(clazz.getSimpleName());
    }

    public BClass(BClassDeclaration declaration, List<SType> generics) {
        this(declaration.getSimpleName(), generics);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<SType> getGenerics() {
        return generics;
    }
}
