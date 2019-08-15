package statref.model.idea;

import com.intellij.psi.PsiElement;

import java.util.AbstractList;

public class IElementList<E extends IElement> extends AbstractList<E> {
    private final PsiElement[] parameters;

    public IElementList(PsiElement[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public E get(int index) {
        return IFactory.getElement(parameters[index]);
    }

    @Override
    public int size() {
        return parameters.length;
    }
}
