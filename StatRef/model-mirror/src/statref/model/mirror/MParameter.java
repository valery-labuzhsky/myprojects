package statref.model.mirror;

import statref.model.SMethodDeclaration;
import statref.model.SParameter;

import javax.lang.model.element.VariableElement;

public class MParameter extends MBaseVariableDeclaration implements SParameter {
    public MParameter(VariableElement element) {
        super(element);
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
