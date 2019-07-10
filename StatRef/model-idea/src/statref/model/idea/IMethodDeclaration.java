package statref.model.idea;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import statref.model.SBaseVariableDeclaration;
import statref.model.SInstruction;
import statref.model.SMethodDeclaration;
import statref.model.SType;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IMethodDeclaration extends IElement<PsiMethod> implements SMethodDeclaration {
    public IMethodDeclaration(PsiMethod method) {
        super(method);
    }

    @NotNull
    public ArrayList<IMethodCall> getCalls() {
        ArrayList<IMethodCall> calls = new ArrayList<>();
        ReferencesSearch.search(getElement(), getElement().getUseScope()).forEach(psiReference -> {
            calls.add(IFactory.getElement(psiReference.getElement().getParent()));
            return true;
        });
        return calls;
    }

    @Override
    public List<SBaseVariableDeclaration> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public SType getReturnType() {
        return IFactory.getType(getElement().getReturnType());
    }

    @Override
    public List<SInstruction> getInstructions() {
        return null;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }

    public IClassDeclaration getClassDeclaration() {
        return IFactory.getElement(getElement().getParent());
    }

}
