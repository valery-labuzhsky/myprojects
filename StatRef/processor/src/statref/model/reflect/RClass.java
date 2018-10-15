package statref.model.reflect;

import statref.model.SClass;
import statref.model.SType;

import java.util.Collections;
import java.util.List;

public class RClass extends RClassRef implements SClass {
    public RClass(Class clazz) {
        super(clazz);
    }

    @Override
    public List<SType> getGenerics() {
        return Collections.emptyList();
    }
}
