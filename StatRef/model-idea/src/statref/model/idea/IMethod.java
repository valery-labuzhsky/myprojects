package statref.model.idea;

import com.intellij.psi.PsiMethodCallExpression;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.members.SMethodDeclaration;

import java.util.List;

public class IMethod extends IExpression implements SMethod {
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

    @Override
    public List<IExpression> getParameters() {
        return new IElementList<>(getElement().getArgumentList().getExpressions());
    }

    @Override
    public SMethodDeclaration findDeclaration() {
        return IFactory.getElement(getElement().resolveMethod());
    }

}
