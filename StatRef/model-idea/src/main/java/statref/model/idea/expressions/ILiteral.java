package statref.model.idea.expressions;

import com.intellij.psi.PsiLiteralExpression;
import statref.model.expressions.SLiteral;

public class ILiteral extends IExpression implements SLiteral {
    public ILiteral(PsiLiteralExpression expression) {
        super(expression);
    }

    @Override
    public PsiLiteralExpression getElement() {
        return (PsiLiteralExpression) super.getElement();
    }

    @Override
    public Object getValue() {
        return getElement().getValue();
    }

}
