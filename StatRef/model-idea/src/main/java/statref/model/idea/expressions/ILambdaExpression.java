package statref.model.idea.expressions;

import com.intellij.psi.PsiLambdaExpression;

/**
 * Created on 22.07.2020.
 *
 * @author unicorn
 */
public class ILambdaExpression extends IExpression {
    public ILambdaExpression(PsiLambdaExpression expression) {
        super(expression);
    }
}
