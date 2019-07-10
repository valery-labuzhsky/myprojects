package statref.model.idea;

import com.intellij.psi.PsiIfStatement;

public class IIfStatement extends IStatement<PsiIfStatement> {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    public IStatement getElseBranch() {
        return (IStatement) IFactory.getElement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return (IStatement) IFactory.getElement(getElement().getThenBranch());
    }

}
