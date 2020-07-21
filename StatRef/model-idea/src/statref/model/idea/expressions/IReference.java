package statref.model.idea.expressions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
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

    public static IReference create(PsiReferenceExpression expression) {
        PsiElement declaration = expression.resolve();
        if (declaration instanceof PsiLocalVariable) {
            return new ILocalVariable(expression);
        } else if (declaration instanceof PsiField) {
            return new IField(expression);
        } else {
            log.error(expression + ": is not supported");
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
        // TODO be more accurate with replace, it may require moving some dependencies too
        this.getElement().replace(((IExpression) expression).getElement());
    }

    // TODO it may have different types of declaration
    //  it may still return one but it will be different in many aspects
    //  so I can just throw an old one away
    //  why not?
    //  I may use smarter approach in the future if I like to
    //  I'm trying to answer question, what implementation of LocalVariable would be
    //  let's just create a child class
    //  I need declaration method, which I'll use to create a reference of required class
    //  will this method be appropriate? why not? I just need to override it

    @Override
    public String getName() {
        return getElement().getReferenceName();
    }

    public String toString() {
        return "" + getElement();
    }
}
