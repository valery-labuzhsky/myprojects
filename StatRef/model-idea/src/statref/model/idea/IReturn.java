package statref.model.idea;

import com.intellij.psi.PsiReturnStatement;
import statref.model.SReturn;
import statref.model.expression.SExpression;

public class IReturn extends IStatement<PsiReturnStatement> implements SReturn {
    public IReturn(PsiReturnStatement statement) {
        super(statement);
    }

    @Override
    public SExpression getExpression() {
        return IFactory.getElement(getElement().getReturnValue());
    }
}
