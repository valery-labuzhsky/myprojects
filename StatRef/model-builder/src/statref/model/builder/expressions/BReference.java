package statref.model.builder.expressions;

import statref.model.expressions.SReference;

/**
 * Created on 29.11.2020.
 *
 * @author unicorn
 */
public abstract class BReference extends BExpression implements SReference {
    protected final String name;

    public BReference(String name) {
        this.name = name;
    }

    public BReference(SReference reference) {
        this(reference.getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
