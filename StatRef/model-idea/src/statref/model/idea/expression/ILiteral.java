package statref.model.idea.expression;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import statref.model.idea.IExpression;

public class ILiteral extends IExpression<PsiLiteralExpression> {
    public ILiteral(PsiExpression expression) {
        super((PsiLiteralExpression) expression);
    }
}
