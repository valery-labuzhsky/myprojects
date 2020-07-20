package statref.model.builder.expressions;

import statref.model.expressions.SField;
import statref.model.types.SType;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BField extends BExpression implements SField {
    private final SType type;
    // TODO it's either typename for int.class, or variable.field
    // TODO so we must create something common for those things
    // TODO actually, int is a variable here, sort of
    private final String name;

    public BField(SType type, String name) {
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
