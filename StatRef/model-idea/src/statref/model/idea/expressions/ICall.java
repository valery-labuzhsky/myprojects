package statref.model.idea.expressions;

import com.intellij.psi.PsiCallExpression;
import statref.model.idea.IElementList;
import statref.model.idea.IFactory;
import statref.model.members.SMethodDeclaration;

import java.util.List;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public abstract class ICall extends IExpression {
    public ICall(PsiCallExpression expression) {
        super(expression);
    }

    @Override
    public PsiCallExpression getElement() {
        return (PsiCallExpression) super.getElement();
    }

    public SMethodDeclaration findDeclaration() {
        return IFactory.getElement(getElement().resolveMethod());
    }

    public List<IExpression> getParameters() {
        return new IElementList<>(getElement().getArgumentList().getExpressions());
    }
}
