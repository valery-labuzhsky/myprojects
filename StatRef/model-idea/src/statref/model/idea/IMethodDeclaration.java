package statref.model.idea;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import statref.model.SMethodDeclaration;
import statref.model.SStatement;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IMethodDeclaration extends IElement<PsiMethod> implements SMethodDeclaration {
    public IMethodDeclaration(PsiMethod method) {
        super(method);
    }

    @NotNull
    public ArrayList<IMethod> getCalls() {
        ArrayList<IMethod> calls = new ArrayList<>();
        ReferencesSearch.search(getElement(), getElement().getUseScope()).forEach(psiReference -> {
            calls.add(getElement(psiReference.getElement().getParent()));
            return true;
        });
        return calls;
    }

    @Override
    public List<IParameter> getParameters() {
        return new IElementList<>(getElement().getParameterList().getParameters());
    }

    public int getParameterIndex(IParameter parameter) {
        return getElement().getParameterList().getParameterIndex(parameter.getElement());
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    @Override
    public IType getReturnType() {
        return IFactory.getType(getElement().getReturnType());
    }

    @Override
    public List<SStatement> getInstructions() {
        return null;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }

}
