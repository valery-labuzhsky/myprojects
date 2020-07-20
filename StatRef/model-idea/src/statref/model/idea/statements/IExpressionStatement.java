package statref.model.idea.statements;

import com.intellij.psi.PsiExpressionStatement;

public class IExpressionStatement extends IStatement {
    public IExpressionStatement(PsiExpressionStatement statement) {
        super(statement);
    }
}
