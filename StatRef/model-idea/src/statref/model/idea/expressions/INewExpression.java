package statref.model.idea.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNewExpression;
import statref.model.idea.IClassReference;
import statref.model.idea.IFactory;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public class INewExpression extends IExpression {
    public INewExpression(PsiNewExpression expression) {
        super(expression);
    }

    @Override
    public PsiNewExpression getElement() {
        return (PsiNewExpression) super.getElement();
    }

    public IClassReference getClassReference() {
        return IFactory.getElement(getElement().getClassReference());
    }

    @Override
    public void replaceIt(IReference reference) {
        PsiElement replacedPsiElement = reference.getElement().replace(getElement());
        if (getClassReference().isDiamond()) {
            INewExpression replacedElement = IFactory.getElement(replacedPsiElement);
            replacedElement.getClassReference().setParameters(getClassReference().resolveParameters());
        }
    }
}
