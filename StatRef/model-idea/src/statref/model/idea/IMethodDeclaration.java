package statref.model.idea;

import com.intellij.psi.PsiMethod;
import statref.model.SBaseVariableDeclaration;
import statref.model.SInstruction;
import statref.model.SMethodDeclaration;
import statref.model.SType;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.List;

public class IMethodDeclaration extends IElement<PsiMethod> implements SMethodDeclaration {
    public IMethodDeclaration(PsiMethod method) {
        super(method);
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
        return null;
    }

    @Override
    public List<SInstruction> getInstructions() {
        return null;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }
}
