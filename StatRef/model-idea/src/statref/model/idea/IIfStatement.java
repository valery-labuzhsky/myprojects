package statref.model.idea;

import com.intellij.psi.PsiIfStatement;

public class IIfStatement extends IStatement<PsiIfStatement> {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    public IStatement getElseBranch() {
        return IFactory.getElement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return IFactory.getElement(getElement().getThenBranch());
    }

}
