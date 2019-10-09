package statref.model.idea;

import com.intellij.psi.PsiReturnStatement;
import statref.model.statements.SReturn;
import statref.model.expressions.SExpression;

public class IReturn extends IStatement<PsiReturnStatement> implements SReturn {
    public IReturn(PsiReturnStatement statement) {
        super(statement);
    }

    @Override
    public SExpression getExpression() {
        return getElement(getElement().getReturnValue());
    }
}
