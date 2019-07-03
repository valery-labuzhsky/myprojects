package statref.model.idea.expression;

import com.intellij.psi.PsiLiteralExpression;
import statref.model.idea.IExpression;

public class ILiteral extends IExpression<PsiLiteralExpression> {
    public ILiteral(PsiLiteralExpression expression) {
        super(expression);
    }

    public Object getValue() {
        return getElement().getValue();
    }

    @Override
    public Object signature() {
        return getValue();
    }
}
