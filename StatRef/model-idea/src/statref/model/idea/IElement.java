package statref.model.idea;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import statref.model.SElement;

import java.util.Objects;

public abstract class IElement implements SElement {
    private final PsiElement element;

    public IElement(PsiElement element) {
        this.element = element;
    }

    public PsiElement getElement() {
        return element;
    }

    public boolean contains(IElement element) {
        return getElement().getTextRange().contains(element.getElement().getTextRange());
    }

    @Override
    public boolean after(SElement element) {
        return getElement().getTextRange().getStartOffset() > ((IElement) element).getElement().getTextRange().getEndOffset();
    }

    @Override
    public boolean before(SElement element) {
        return getElement().getTextRange().getEndOffset() < ((IElement) element).getElement().getTextRange().getStartOffset();
    }

    @Override
    public IElement getParent() {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiExpressionList) {
            parent = parent.getParent();
        }
        return getElement(parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IElement iElement = (IElement) o;
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

    protected <T extends IElement> T getElement(PsiElement element) {
        return IFactory.getElement(element);
    }
}
