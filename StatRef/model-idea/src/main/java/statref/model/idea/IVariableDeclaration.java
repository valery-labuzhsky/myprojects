package statref.model.idea;

import com.intellij.psi.PsiVariable;
import statref.model.idea.expressions.IExpression;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import java.util.Collection;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public abstract class IVariableDeclaration extends IElement {
    public IVariableDeclaration(PsiVariable element) {
        super(element);
    }

    @Override
    public PsiVariable getElement() {
        return (PsiVariable) super.getElement();
    }

    public IExpression getInitializer() {
        return getElement(getElement().getInitializer());
    }

    public String getName() {
        return getElement().getName();
    }

    public Collection<Modifier> getModifiers() {
        return null;
    }

    public SType getType() {
        return null;
    }
}
