package statref.model.mirror;

import statref.model.members.SParameter;

import javax.lang.model.element.VariableElement;

public class MParameter extends MVariableDeclaration implements SParameter {
    public MParameter(VariableElement element) {
        super(element);
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
