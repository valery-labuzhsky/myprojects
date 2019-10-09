package statref.model.mirror;

import statref.model.members.SFieldDeclaration;
import statref.model.expressions.SExpression;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class MFieldDeclaration extends MBaseVariableDeclaration implements SFieldDeclaration {
    public MFieldDeclaration(Element element) {
        super((VariableElement) element);
    }

    @Override
    public SExpression getInitializer() {
        return null;
    }
}
