package statref.model.mirror;

import statref.model.SClassMemeber;
import statref.model.SFieldDeclaration;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class MFieldDeclaration extends MBaseVariableDeclaration implements SFieldDeclaration {
    public MFieldDeclaration(Element element) {
        super((VariableElement) element);
    }
}
