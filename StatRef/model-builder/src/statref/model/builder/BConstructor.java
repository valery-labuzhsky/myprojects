package statref.model.builder;

import statref.model.SClass;
import statref.model.expression.SConstructor;
import statref.model.expression.SVariable;

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
