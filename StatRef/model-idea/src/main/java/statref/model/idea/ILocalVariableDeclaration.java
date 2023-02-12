package statref.model.idea;

import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.annotations.NotNull;
import statref.model.SLocalVariableDeclaration;
import statref.model.idea.expressions.ILocalVariable;
import statref.model.idea.expressions.IReferenceFinder;

public class ILocalVariableDeclaration extends IVariableDeclaration implements SLocalVariableDeclaration, IInitializer {

    public ILocalVariableDeclaration(PsiLocalVariable element) {
        super(element);
    }

    @Override
    public PsiLocalVariable getElement() {
        return (PsiLocalVariable) super.getElement();
    }

    @NotNull
    @Override
    public ILocalVariableDeclaration declaration() {
        return this;
    }

    public IReferenceFinder<ILocalVariable> find() {
        return new IReferenceFinder<>(declaration().getElement(), e -> new ILocalVariable(e));
    }
}
