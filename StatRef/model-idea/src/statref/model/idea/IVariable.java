package statref.model.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import statref.model.expressions.SExpression;
import statref.model.expressions.SVariable;

public class IVariable extends IExpression<PsiReferenceExpression> implements SVariable, IVariableReference {

    public IVariable(PsiReferenceExpression element) {
        super(element);
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
        this.getElement().replace(((IExpression<?>)expression).getElement());
    }

    @Override
    @NotNull
    public IVariableDeclaration declaration() {
        // TODO check that element is resolved
        PsiElement declaration = this.getElement().resolve();
        // TODO check that it's local variable
        PsiLocalVariable localVariable = (PsiLocalVariable) declaration;

        return new IVariableDeclaration(localVariable);
    }

    @Override
    public String getName() {
        return getElement().getReferenceName();
    }

    public String toString() {
        return "" + getElement();
    }
}
