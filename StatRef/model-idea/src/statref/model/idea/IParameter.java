package statref.model.idea;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import statref.model.members.SParameter;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import java.util.Collection;

public class IParameter extends IElement implements SParameter {
    public IParameter(PsiParameter element) {
        super(element);
    }

    @Override
    public PsiParameter getElement() {
        return (PsiParameter) super.getElement();
    }

    @Override
    public int getIndex() {
        PsiParameterList parameters = (PsiParameterList) getElement().getParent();
        return parameters.getParameterIndex(getElement());
    }

    @Override
    public IMethodDeclaration getParent() {
        return getElement(getElement().getParent().getParent());
    }

    @Override
    public SType getType() {
        return ITypes.getType(getElement().getType());
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }
}
