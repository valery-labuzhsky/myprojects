package statref.model.idea.expression;

import com.intellij.psi.PsiLiteralExpression;
import statref.model.expressions.SLiteral;
import statref.model.idea.IExpression;

public class ILiteral extends IExpression<PsiLiteralExpression> implements SLiteral {
    public ILiteral(PsiLiteralExpression expression) {
        super(expression);
    }

    @Override
    public Object getValue() {
        return getElement().getValue();
    }

}
