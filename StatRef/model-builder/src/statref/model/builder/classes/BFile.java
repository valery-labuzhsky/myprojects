package statref.model.builder.classes;

import statref.model.types.SClass;
import statref.model.classes.SClassDeclaration;
import statref.model.classes.SFile;
import statref.model.classes.SPackage;
import statref.model.builder.types.BClass;

import java.util.ArrayList;
import java.util.List;

public class BFile implements SFile {
    private final SPackage package_;
    private ArrayList<SClassDeclaration> classes;
    private ArrayList<SClass> imports;

    public BFile(SPackage package_) {
        this.package_ = package_;
        classes = new ArrayList<>();
        imports = new ArrayList<>();
    }

    @Override
    public SPackage getPackage() {
        return package_;
    }

    @Override
    public List<SClass> getImports() {
        return imports;
    }

    @Override
    public List<SClassDeclaration> getClasses() {
        return classes;
    }

    public BFile class_(BClassDeclaration classDeclaration) {
        classes.add(classDeclaration);
        return this;
    }

    public BFile import_(BClass clazz) {
        imports.add(clazz);
        return this;
    }
}
