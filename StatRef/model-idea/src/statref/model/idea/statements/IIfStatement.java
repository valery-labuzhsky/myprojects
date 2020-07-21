package statref.model.idea.statements;

import com.intellij.psi.PsiIfStatement;
import statref.model.idea.expressions.IExpression;

public class IIfStatement extends IStatement {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    @Override
    public PsiIfStatement getElement() {
        return (PsiIfStatement) super.getElement();
    }

    public IExpression getCondition() {
        return getElement(getElement().getCondition());
    }

    public IStatement getElseBranch() {
        return (IStatement) getElement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return (IStatement) getElement(getElement().getThenBranch());
    }

}
