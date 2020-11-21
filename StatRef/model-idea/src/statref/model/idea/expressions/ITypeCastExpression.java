package statref.model.idea.expressions;

import com.intellij.psi.PsiTypeCastExpression;

/**
 * Created on 21.11.2020.
 *
 * @author unicorn
 */
public class ITypeCastExpression extends IExpression {
    public ITypeCastExpression(PsiTypeCastExpression expression) {
        super(expression);
    }
}
