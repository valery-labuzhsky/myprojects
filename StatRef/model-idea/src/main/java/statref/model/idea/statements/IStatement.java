package statref.model.idea.statements;

import com.intellij.psi.PsiStatement;
import statref.model.idea.IElement;

public class IStatement extends IElement {
    public IStatement(PsiStatement element) {
        super(element);
    }

    @Override
    public PsiStatement getElement() {
        return (PsiStatement) super.getElement();
    }
}
