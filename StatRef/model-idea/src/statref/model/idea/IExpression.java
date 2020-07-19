package statref.model.idea;

import com.intellij.psi.PsiExpression;
import statref.model.expressions.SExpression;

public abstract class IExpression extends IElement implements SExpression {
    public IExpression(PsiExpression expression) {
        super(expression);
    }

    @Override
    public PsiExpression getElement() {
        return (PsiExpression) super.getElement();
    }
}
