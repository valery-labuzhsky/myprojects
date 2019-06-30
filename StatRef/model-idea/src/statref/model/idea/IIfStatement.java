package statref.model.idea;

import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiLoopStatement;

public class IIfStatement extends IStatement<PsiIfStatement> {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    public IStatement getElseBranch() {
        return IFactory.getStatement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return IFactory.getStatement(getElement().getThenBranch());
    }

}
