package statref.model.builder.expressions;

import statref.model.expressions.SFieldUsage;
import statref.model.types.SType;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BFieldUsage extends BExpression implements SFieldUsage {
    private final SType type;
    // TODO it's either typename for int.class, or variable.field
    // TODO so we must create something common for those things
    // TODO actually, int is a variable here, sort of
    private final String name;

    public BFieldUsage(SType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public SType getQualifier() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}