package statref.model.idea;

import com.intellij.psi.PsiLocalVariable;
import statref.model.SType;
import statref.model.SVariableDeclaration;
import statref.model.expression.SVariable;

import javax.lang.model.element.Modifier;
import java.util.Collection;

public class IVariableDeclaration extends IElement<PsiLocalVariable> implements SVariableDeclaration, IInitializer {

    public IVariableDeclaration(PsiLocalVariable element) {
        super(element);
    }

    @Override
    public IExpression getInitializer() {
        return IFactory.getExpression(getElement().getInitializer());
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }

    @Override
    public SType getType() {
        return null;
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    @Override
    public SVariable usage() {
        return null;
    }
}
