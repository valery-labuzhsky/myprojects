package statref.model.mirror;

import statref.model.SVariable;

import javax.lang.model.element.VariableElement;

public class MVariable implements SVariable {
    private final VariableElement element;

    public MVariable(VariableElement element) {
        this.element = element;
    }

    @Override
    public String getName() {
        return element.getSimpleName().toString();
    }
}
