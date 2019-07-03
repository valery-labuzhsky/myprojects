package statref.model.idea;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;

public class IParameter extends IElement<PsiParameter> {
    public IParameter(PsiParameter element) {
        super(element);
    }

    public int getIndex() {
        PsiParameterList parameters = (PsiParameterList) getElement().getParent();
        return parameters.getParameterIndex(getElement());
    }

    public IElement getMethod() {
        return IFactory.getElement(getElement().getParent().getParent());
    }
}
