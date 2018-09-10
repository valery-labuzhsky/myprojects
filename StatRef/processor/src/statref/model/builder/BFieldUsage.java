package statref.model.builder;

import statref.model.SClass;
import statref.model.SFieldUsage;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BFieldUsage implements SFieldUsage {
    private final SClass clazz; // TODO it covers static field only
    private final String name;

    public BFieldUsage(SClass clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public SClass getType() {
        return clazz;
    }

    @Override
    public String getName() {
        return name;
    }
}
