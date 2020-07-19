package statref.model.builder.members;

import statref.model.builder.BElement;
import statref.model.expressions.SConstructor;
import statref.model.expressions.SLocalVariable;
import statref.model.types.SClass;

import java.util.ArrayList;
import java.util.List;

public class BConstructor extends BElement implements SConstructor {
    private final SClass parent;
    private final ArrayList<SLocalVariable> parameters = new ArrayList<>();

    public BConstructor(SClass parent) {
        this.parent = parent;
    }

    @Override
    public SClass getSClass() {
        return parent;
    }

    @Override
    public List<SLocalVariable> getParameters() {
        return parameters;
    }
}
