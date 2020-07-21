package statref.model.idea.expressions;

import com.intellij.psi.PsiConditionalExpression;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public class IConditional extends IExpression {
    public IConditional(PsiConditionalExpression expression) {
        super(expression);
    }

    public IExpression getCondition() {
        return getElement(getElement().getCondition());
    }

    public IExpression getThen() {
        return getElement(getElement().getThenExpression());
    }

    public IExpression getElse() {
        return getElement(getElement().getElseExpression());
    }

    @Override
    public PsiConditionalExpression getElement() {
        return (PsiConditionalExpression) super.getElement();
    }
}
