package statref.model.idea;

import com.intellij.psi.PsiIfStatement;

public class IIfStatement extends IStatement {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    @Override
    public PsiIfStatement getElement() {
        return (PsiIfStatement) super.getElement();
    }

    public IStatement getElseBranch() {
        return (IStatement) getElement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return (IStatement) getElement(getElement().getThenBranch());
    }

}
