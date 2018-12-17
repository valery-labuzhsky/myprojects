package statref.model.idea;

import com.intellij.psi.PsiElement;
import statref.model.SElement;

public abstract class IElement<PSI extends PsiElement> implements SElement {
    private final PSI element;

    public IElement(PSI element) {
        this.element = element;
    }

    public PSI getElement() {
        return element;
    }

    public boolean before(SElement element) {
        return getElement().getTextOffset() < ((IElement)element).getElement().getTextOffset();
    }

    public boolean after(SElement element) {
        return !before(element);
    }

    @Override
    public SElement getParent() {
        return IFactory.getElement(element.getParent());
    }
}
