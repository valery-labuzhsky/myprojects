package statref.model.mirror;

import statref.model.SFieldDeclaration;
import statref.model.expression.SExpression;

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
