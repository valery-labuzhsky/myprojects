package statref.model.idea;

import com.intellij.psi.PsiStatement;

public class IStatement extends IElement {
    public IStatement(PsiStatement element) {
        super(element);
    }

    @Override
    public PsiStatement getElement() {
        return (PsiStatement) super.getElement();
    }
}
