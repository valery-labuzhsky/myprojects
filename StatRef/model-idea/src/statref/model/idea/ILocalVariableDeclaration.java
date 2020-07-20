package statref.model.idea;

import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.annotations.NotNull;
import statref.model.SLocalVariableDeclaration;
import statref.model.idea.expressions.IExpression;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import java.util.Collection;

public class ILocalVariableDeclaration extends IElement implements SLocalVariableDeclaration, IInitializer {

    public ILocalVariableDeclaration(PsiLocalVariable element) {
        super(element);
    }

    @Override
    public PsiLocalVariable getElement() {
        return (PsiLocalVariable) super.getElement();
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
    public ILocalVariableDeclaration declaration() {
        return this;
    }

    @Override
    public String getName() {
        return getElement().getName();
    }
}
