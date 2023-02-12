package statref.model.idea.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNewExpression;
import statref.model.classes.SClassReference;
import statref.model.expressions.SNew;
import statref.model.idea.IFactory;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public class INew extends ICall implements SNew {
    public INew(PsiNewExpression expression) {
        super(expression);
    }

    @Override
    public PsiNewExpression getElement() {
        return (PsiNewExpression) super.getElement();
    }

    @Override
    public SClassReference getClassReference() {
        return IFactory.getElement(getElement().getClassReference());
    }

    @Override
    public void replaceIt(IReference reference) {
        PsiElement replacedPsiElement = reference.getElement().replace(getElement());
        if (getClassReference().isDiamond()) {
            INew replacedElement = IFactory.getElement(replacedPsiElement);
            replacedElement.getClassReference().setParameters(getClassReference().resolveParameters());
        }
    }
}
