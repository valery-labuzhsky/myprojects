package statref.model.mirror;

import statref.model.members.SParameterDeclaration;

import javax.lang.model.element.VariableElement;

public class MParameterDeclaration extends MVariableDeclaration implements SParameterDeclaration {
    public MParameterDeclaration(VariableElement element) {
        super(element);
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
