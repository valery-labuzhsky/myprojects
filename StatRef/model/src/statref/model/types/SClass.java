package statref.model.types;

import statref.model.classes.SClassDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO it must be an interface
//  and it must have implementations in different modules
//  maybe not a single one, but many of them
public class SClass extends SType {
    // TODO it should actually always know its package or enclosing class from the context
    // TODO it should be split between primitive and declared types
    private final String name;
    private final List<SType> generics = new ArrayList<>();

    public SClass(String name) {
        this.name = name;
    }

    public SClass(String name, List<SType> generics) {
        this(name);
        this.generics.addAll(generics);
    }

    public SClass(String name, SType... generics) {
        this(name, Arrays.asList(generics));
    }

    public SClass(SClassDeclaration clazz) {
        this(clazz.getSimpleName());
    }

    public SClass(SClassDeclaration declaration, List<SType> generics) {
        this(declaration.getSimpleName(), generics);
    }

    public SClass(Class clazz) {
        this(clazz.getSimpleName());
    }

    public String getName() {
        return name;
    }

    public List<SType> getGenerics() {
        return generics;
    }

    public Class<?> getJavaClass() {
        return null;
    }

}
