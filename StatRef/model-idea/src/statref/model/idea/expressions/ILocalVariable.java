package statref.model.idea.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import statref.model.expressions.SExpression;
import statref.model.expressions.SLocalVariable;
import statref.model.idea.ILocalVariableDeclaration;
import statref.model.idea.IVariableReference;

public class ILocalVariable extends IExpression implements SLocalVariable, IVariableReference {

    public ILocalVariable(PsiReferenceExpression element) {
        super(element);
    }

    @Override
    public PsiReferenceExpression getElement() {
        return (PsiReferenceExpression) super.getElement();
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

    @Override
    @NotNull
    public ILocalVariableDeclaration declaration() {
        // TODO check that element is resolved
        PsiElement declaration = this.getElement().resolve();
        // TODO check that it's local variable
        PsiLocalVariable localVariable = (PsiLocalVariable) declaration;

        return new ILocalVariableDeclaration(localVariable);
    }

    @Override
    public String getName() {
        return getElement().getReferenceName();
    }

    public String toString() {
        return "" + getElement();
    }
}
