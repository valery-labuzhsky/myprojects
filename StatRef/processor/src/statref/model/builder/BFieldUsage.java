package statref.model.builder;

import statref.model.SFieldUsage;
import statref.model.SType;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BFieldUsage extends BExpression implements SFieldUsage {
    private final SType type;
    // TODO it's either typename for int.class, or variable.field
    // TODO so we must create something common for those things
    private final String name;

    public BFieldUsage(SType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public SType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
