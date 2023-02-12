package statref.model.idea.statements;

import com.intellij.psi.PsiLoopStatement;

public abstract class ILoopStatement extends IStatement {
    public ILoopStatement(PsiLoopStatement element) {
        super(element);
    }

    @Override
    public PsiLoopStatement getElement() {
        return (PsiLoopStatement) super.getElement();
    }

    public IStatement getBody() {
        return (IStatement) getElement(getElement().getBody());
    }

}
