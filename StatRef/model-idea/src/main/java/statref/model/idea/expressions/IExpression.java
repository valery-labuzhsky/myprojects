package statref.model.idea.expressions;

import com.intellij.psi.PsiExpression;
import statref.model.expressions.SExpression;
import statref.model.idea.IElement;

public abstract class IExpression extends IElement implements SExpression {
    public IExpression(PsiExpression expression) {
        super(expression);
    }

    @Override
    public PsiExpression getElement() {
        return (PsiExpression) super.getElement();
    }

    public void replaceIt(IReference reference) {
        reference.getElement().replace(getElement());
    }
}
