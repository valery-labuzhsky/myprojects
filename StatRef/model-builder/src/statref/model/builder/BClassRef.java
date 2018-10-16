package statref.model.builder;

import statref.model.SClassDeclaration;
import statref.model.SClassRef;
import statref.model.SPackage;

public class BClassRef implements SClassRef { // TODO do I still needed?
    private final SPackage pkg;
    private final String name;

    public BClassRef(SPackage pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public BClassRef(SClassDeclaration clazz) {
        this(clazz.getPackage(), clazz.getSimpleName());
    }

    @Override
    public SPackage getPackage() {
        return pkg;
    }

    @Override
    public String getSimpleName() {
        return name;
    }
}
