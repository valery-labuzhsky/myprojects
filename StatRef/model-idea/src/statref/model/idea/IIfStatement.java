package statref.model.idea;

import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiLoopStatement;

public class IIfStatement extends IStatement<PsiIfStatement> {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    public IStatement getElseBranch() {
        return (IStatement) IFactory.getStatement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return (IStatement) IFactory.getStatement(getElement().getThenBranch());
    }

}
