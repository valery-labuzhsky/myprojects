package statref.model.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface IVariableReference {

    PsiElement getElement();

    @NotNull
    ILocalVariableDeclaration declaration();

    String getName();

    @NotNull
    default ArrayList<ILocalVariable> mentions() {
        ArrayList<ILocalVariable> usages = new ArrayList<>();
        ReferencesSearch.search(declaration().getElement(), this.getElement().getUseScope()).forEach(psiReference -> {
            usages.add(new ILocalVariable((PsiReferenceExpression) psiReference));
            return true;
        });
        return usages;
    }

    @NotNull
    default ArrayList<ILocalVariable> valueUsages() {
        ArrayList<ILocalVariable> valueUsages = new ArrayList<>();
        for (ILocalVariable mention : mentions()) {
            if (!mention.isAssignment()) {
                valueUsages.add(mention);
            }
        }
        return valueUsages;
    }
}
