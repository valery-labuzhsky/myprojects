package statref.model.idea;

import com.intellij.psi.PsiExpressionStatement;

public class IExpressionStatement extends IStatement {
    public IExpressionStatement(PsiExpressionStatement statement) {
        super(statement);
    }
}
