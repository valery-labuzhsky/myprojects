package statref.model.idea;

import com.intellij.psi.PsiMethodCallExpression;

public class IMethodCall extends IExpression<PsiMethodCallExpression> {
    public IMethodCall(PsiMethodCallExpression expression) {
        super(expression);
    }

    public IExpression getExpression(IParameter parameter) {
        return IFactory.getElement(getElement().getArgumentList().getExpressions()[parameter.getIndex()]);
    }
}
