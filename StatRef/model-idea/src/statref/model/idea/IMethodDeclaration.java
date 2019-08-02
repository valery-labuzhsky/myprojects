package statref.model.idea;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import statref.model.SMethodDeclaration;
import statref.model.SStatement;

import javax.lang.model.element.Modifier;
import java.util.AbstractList;
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
    public List<IParameter> getParameters() {
        PsiParameter[] parameters = getElement().getParameterList().getParameters();
        return new AbstractList<IParameter>() {
            @Override
            public IParameter get(int index) {
                return IFactory.getElement(parameters[index]);
            }

            @Override
            public int size() {
                return parameters.length;
            }
        };
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
