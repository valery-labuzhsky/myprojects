package statref.model.mirror;

import statref.model.expressions.SLocalVariable;

import javax.lang.model.element.VariableElement;

public class MLocalVariable extends MElement<VariableElement> implements SLocalVariable {
    public MLocalVariable(VariableElement element) {
        super(element);
    }

    @Override
    public String getName() {
        return getElement().getSimpleName().toString();
    }
}
