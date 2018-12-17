package statref.model.mirror;

import statref.model.expression.SVariable;

import javax.lang.model.element.VariableElement;

public class MVariable extends MElement<VariableElement> implements SVariable {
    public MVariable(VariableElement element) {
        super(element);
    }

    @Override
    public String getName() {
        return getElement().getSimpleName().toString();
    }
}
