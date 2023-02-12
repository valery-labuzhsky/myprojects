package statref.model.idea;

import com.intellij.psi.PsiClass;
import statref.model.classes.SClassDeclaration;

public class IClassDeclaration extends IElement implements SClassDeclaration {
    public IClassDeclaration(PsiClass element) {
        super(element);
    }

    @Override
    public PsiClass getElement() {
        return (PsiClass) super.getElement();
    }

    @Override
    public String getSimpleName() {
        return getElement().getName();
    }
}
