package statref.model.idea.expressions;

import com.intellij.psi.PsiPolyadicExpression;

/**
 * Created on 26.11.2020.
 *
 * @author unicorn
 */
public class IPolyadicExpressionExpression extends IExpression {
    public IPolyadicExpressionExpression(PsiPolyadicExpression expression) {
        super(expression);
    }
}
