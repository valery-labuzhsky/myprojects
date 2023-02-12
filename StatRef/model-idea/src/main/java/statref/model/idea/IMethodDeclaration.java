package statref.model.idea;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.expressions.SCall;
import statref.model.expressions.SMethod;
import statref.model.fragment.Fragment;
import statref.model.fragment.Place;
import statref.model.idea.expressions.ICall;
import statref.model.members.SMethodDeclaration;
import statref.model.statements.SStatement;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IMethodDeclaration extends IElement implements SMethodDeclaration {
    public IMethodDeclaration(PsiMethod method) {
        super(method);
    }

    @Override
    public PsiMethod getElement() {
        return (PsiMethod) super.getElement();
    }

    @NotNull
    public ArrayList<ICall> getCalls() {
        ArrayList<ICall> calls = new ArrayList<>();
        ReferencesSearch.search(getElement(), getElement().getUseScope()).forEach(psiReference -> {
            calls.add(getElement(psiReference.getElement().getParent()));
            return true;
        });
        return calls;
    }

    @Override
    public List<IParameterDeclaration> getParameters() {
        return new IElementList<>(getElement().getParameterList().getParameters());
    }

    public int getParameterIndex(IParameterDeclaration parameter) {
        return getElement().getParameterList().getParameterIndex(parameter.getElement());
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    @Override
    public SType getReturnType() {
        if (isConstructor()) {
            return getContainingClass().usage();
        }
        return ITypes.getType(getElement().getReturnType());
    }

    @Nullable
    public IClassDeclaration getContainingClass() {
        return IFactory.getElement(getElement().getContainingClass());
    }

    @Override
    public List<SStatement> getInstructions() {
        return null;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }

    public ParameterPlace getPlace(IParameterDeclaration parameter) {
        return new ParameterPlace(getParameterIndex(parameter));
    }

    public boolean isConstructor() {
        return getElement().getReturnType() == null;
    }

    public static class ParameterPlace implements Place<IParameterDeclaration> {
        private final int index;

        // TODO implement everything
        public ParameterPlace(int index) {
            this.index = index;
        }

        @Override
        public String getName(Fragment fragment) {
            return null;
        }

        @Override
        public SType getType(Fragment fragment) {
            return null;
        }

        @Override
        public IParameterDeclaration get(Fragment fragment) {
            return null;
        }

        @Override
        public void set(Fragment fragment, IParameterDeclaration value) {

        }

        public SMethod.Parameter getMethodPlace() {
            return SCall.getParameterPlace(index);
        }

    }
}
