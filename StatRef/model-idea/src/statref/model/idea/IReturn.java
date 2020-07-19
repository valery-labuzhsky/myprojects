package statref.model.idea;

import com.intellij.psi.PsiReturnStatement;
import statref.model.expressions.SExpression;
import statref.model.statements.SReturn;

public class IReturn extends IStatement implements SReturn {
    public IReturn(PsiReturnStatement statement) {
        super(statement);
    }

    @Override
    public PsiReturnStatement getElement() {
        return (PsiReturnStatement) super.getElement();
    }

    @Override
    public SExpression getExpression() {
        return getElement(getElement().getReturnValue());
    }
}
