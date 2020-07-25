package statref.model.idea.expressions;

import com.intellij.psi.PsiNewExpression;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public class INewExpression extends IExpression {
    public INewExpression(PsiNewExpression expression) {
        super(expression);
    }
}
