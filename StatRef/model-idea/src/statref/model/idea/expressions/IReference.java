package statref.model.idea.expressions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import statref.model.SElement;
import statref.model.expressions.SExpression;
import statref.model.expressions.SReference;
import statref.model.idea.IFactory;
import statref.model.idea.IVariableDeclaration;

/**
 * Created on 20.07.2020.
 *
 * @author unicorn
 */
public abstract class IReference extends IExpression implements SReference, IVariableReference {
    private static final Logger log = Logger.getInstance(IReference.class);

    public IReference(PsiReferenceExpression expression) {
        super(expression);
    }

    public static IExpression create(PsiReferenceExpression expression) {
        PsiElement declaration = expression.resolve();
        if (declaration instanceof PsiLocalVariable) {
            return new ILocalVariable(expression);
        } else if (declaration instanceof PsiField) {
            return new IField(expression);
        } else if (declaration instanceof PsiMethod) {
            return IFactory.getElement(expression.getParent());
        } else {
            log.error(declaration + ": is not supported");
            return new IReference(expression) {
            };
        }
    }

    @Override
    public PsiReferenceExpression getElement() {
        return (PsiReferenceExpression) super.getElement();
    }

    @Override
    public IVariableDeclaration declaration() {
        // TODO check that element is resolved
        return IFactory.getElement(this.getElement().resolve());
    }

    public boolean isAssignment() {
        boolean assignment = false;
        SElement parent = getParent();
        if (parent instanceof IAssignment) {
            assignment = ((IAssignment) parent).getVariable().equals(this);
        }
        return assignment;
    }

    public void replace(SExpression expression) {
        // TODO it's not necessary IExpression, it may be builder for example
        IExpression ex = (IExpression) expression;

        PsiElement replacedPsiElement = this.getElement().replace(ex.getElement());

        // TODO now I need moving this if to INewExpression
        //  but I need check if it works first
        if (expression instanceof INewExpression) {
            INewExpression newExpression = (INewExpression) expression;
            if (newExpression.getClassReference().isDiamond()) {
                INewExpression replacedElement = IFactory.getElement(replacedPsiElement);
                replacedElement.getClassReference().setParameters(newExpression.getClassReference().resolveParameters());
            }
        }
    }

    @Override
    public String getName() {
        return getElement().getReferenceName();
    }

    public String toString() {
        return "" + getElement();
    }
}
