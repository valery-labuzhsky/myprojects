package statref.model.reflect;

import statref.model.SClass;
import statref.model.SType;

import java.util.Collections;
import java.util.List;

public class RClass implements SClass {
    private final Class clazz;

    public RClass(Class  clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public List<SType> getGenerics() {
        return Collections.emptyList();
    }
}
