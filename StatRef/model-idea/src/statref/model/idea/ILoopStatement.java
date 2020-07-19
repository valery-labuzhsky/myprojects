package statref.model.idea;

import com.intellij.psi.PsiLoopStatement;

public abstract class ILoopStatement<L extends PsiLoopStatement> extends IStatement {
    public ILoopStatement(L element) {
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
