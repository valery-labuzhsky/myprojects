package statref.model.idea.expressions;

import com.intellij.psi.PsiMethodCallExpression;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;

public class IMethod extends ICall implements SMethod {
    public IMethod(PsiMethodCallExpression expression) {
        super(expression);
    }

    @Override
    public PsiMethodCallExpression getElement() {
        return (PsiMethodCallExpression) super.getElement();
    }

    @Override
    public SExpression getQualifier() {
        return getElement(getElement().getMethodExpression().getQualifierExpression());
    }

    @Override
    public String getName() {
        return getElement().getMethodExpression().getReferenceName();
    }

}
