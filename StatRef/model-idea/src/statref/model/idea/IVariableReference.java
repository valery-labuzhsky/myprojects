package statref.model.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface IVariableReference {

    PsiElement getElement();

    @NotNull
    IVariableDeclaration declaration();

    String getName();

    @NotNull
    default ArrayList<IVariable> mentions() {
        ArrayList<IVariable> usages = new ArrayList<>();
        ReferencesSearch.search(declaration().getElement(), this.getElement().getUseScope()).forEach(psiReference -> {
            usages.add(new IVariable((PsiReferenceExpression) psiReference));
            return true;
        });
        return usages;
    }

    @NotNull
    default ArrayList<IVariable> valueUsages() { // TODO use iterable?
        ArrayList<IVariable> valueUsages = new ArrayList<>();
        for (IVariable mention : mentions()) {
            if (!mention.isAssignment()) {
                valueUsages.add(mention);
            }
        }
        return valueUsages;
    }
}
