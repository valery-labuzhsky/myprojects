package statref.model.idea;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import statref.model.SElement;

import java.util.Objects;

public abstract class IElement<PSI extends PsiElement> implements SElement {
    private final PSI element;

    public IElement(PSI element) {
        this.element = element;
    }

    public PSI getElement() {
        return element;
    }

    public boolean contains(IElement element) {
        return getElement().getTextRange().contains(element.getElement().getTextRange());
    }

    @Override
    public boolean before(SElement element) {
        return getElement().getTextOffset() < ((IElement)element).getElement().getTextOffset();
    }

    @Override
    public IElement getParent() {
        return getElement(element.getParent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IElement<?> iElement = (IElement<?>) o;
        return Objects.equals(element, iElement.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public String toString() {
        return element.toString();
    }

    public Project getProject() {
        return element.getProject();
    }

    public Fragment fragment() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    protected <T extends IElement> T getElement(PsiElement element) {
        return IFactory.getElement(element);
    }
}
