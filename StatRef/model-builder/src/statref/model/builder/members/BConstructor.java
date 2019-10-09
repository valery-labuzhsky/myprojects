package statref.model.builder.members;

import statref.model.types.SClass;
import statref.model.builder.BElement;
import statref.model.expressions.SConstructor;
import statref.model.expressions.SVariable;

import java.util.ArrayList;
import java.util.List;

public class BConstructor extends BElement implements SConstructor {
    private final SClass parent;
    private final ArrayList<SVariable> parameters = new ArrayList<>();

    public BConstructor(SClass parent) {
        this.parent = parent;
    }

    @Override
    public SClass getSClass() {
        return parent;
    }

    @Override
    public List<SVariable> getParameters() {
        return parameters;
    }
}
