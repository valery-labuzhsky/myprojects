package statref.model.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import statref.model.SInitializer;
import statref.model.expression.SExpression;
import statref.model.expression.SVariable;

import java.util.ArrayList;

public class IVariable extends IExpression<PsiReferenceExpression> implements SVariable {

    public IVariable(PsiReferenceExpression element) {
        super(element);
    }

    public boolean isAssignment() {
        boolean assignment = false;
        SElement parent = getParent();
        if (parent instanceof SInitializer) {
            assignment = ((SInitializer) parent).getVariable().equals(this);
        }
        return assignment;
    }

    @NotNull
    public ArrayList<IVariable> mentions() {
        ArrayList<IVariable> usages = new ArrayList<>();
        ReferencesSearch.search(declaration().getElement(), this.getElement().getUseScope()).forEach(psiReference -> {
            usages.add(new IVariable((PsiReferenceExpression) psiReference));
            return true;
        });
        return usages;
    }

    public void replace(SExpression expression) {
        // TODO be more accurate with replace, it may require moving some dependencies too
        this.getElement().replace(((IExpression<?>)expression).getElement());
    }

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

    @Override
    public IElement getParent() {
        PsiElement element = this.getElement().getParent();
        return IFactory.getElement(element);
    }
}
