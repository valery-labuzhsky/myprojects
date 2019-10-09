package statref.model.idea;

import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.annotations.NotNull;
import statref.model.types.SType;
import statref.model.SVariableDeclaration;

import javax.lang.model.element.Modifier;
import java.util.Collection;

public class IVariableDeclaration extends IElement<PsiLocalVariable> implements SVariableDeclaration, IInitializer {

    public IVariableDeclaration(PsiLocalVariable element) {
        super(element);
    }

    @Override
    public IExpression getInitializer() {
        return (IExpression) getElement(getElement().getInitializer());
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return null;
    }

    @Override
    public SType getType() {
        return null;
    }

    @NotNull
    @Override
    public IVariableDeclaration declaration() {
        return this;
    }

    @Override
    public String getName() {
        return getElement().getName();
    }
}
