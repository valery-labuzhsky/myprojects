package statref.model.idea.expression;

import com.intellij.psi.PsiLiteralExpression;
import statref.model.idea.IExpression;
import statref.model.idea.CodeFragment;

public class ILiteral extends IExpression<PsiLiteralExpression> {
    public ILiteral(PsiLiteralExpression expression) {
        super(expression);
    }

    public Object getValue() {
        return getElement().getValue();
    }

    @Override
    public CodeFragment fragment() {
        return new CodeFragment().output(this);
    }
}
