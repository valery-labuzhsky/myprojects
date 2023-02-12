package statref.model.idea.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created on 20.07.2020.
 *
 * @author unicorn
 */
public class IReferenceFinder<R extends IReference> {
    private final PsiElement declaration;
    private final Function<PsiReferenceExpression, R> constructor;

    public IReferenceFinder(PsiElement declaration, Function<PsiReferenceExpression, R> constructor) {
        this.constructor = constructor;
        this.declaration = declaration;
    }

    @NotNull
    ArrayList<R> valueUsages() {
        ArrayList<R> valueUsages = new ArrayList<>();
        for (R mention : mentions()) {
            if (!mention.isAssignment()) {
                valueUsages.add(mention);
            }
        }
        return valueUsages;
    }

    @NotNull
    public ArrayList<R> mentions() {
        ArrayList<R> usages = new ArrayList<>();
        ReferencesSearch.search(declaration, declaration.getUseScope()).forEach(psi -> {
            usages.add(constructor.apply((PsiReferenceExpression) psi));
            return true;
        });
        return usages;
    }

}
