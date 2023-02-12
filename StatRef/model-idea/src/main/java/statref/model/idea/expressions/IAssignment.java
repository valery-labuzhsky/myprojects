package statref.model.idea.expressions;

import com.intellij.psi.PsiAssignmentExpression;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariableDeclaration;

public class IAssignment extends IExpression implements IInitializer {
    public IAssignment(PsiAssignmentExpression assignment) {
        super(assignment);
    }

    @Override
    public PsiAssignmentExpression getElement() {
        return (PsiAssignmentExpression) super.getElement();
    }

    @Override
    public IExpression getInitializer() {
        return (IExpression) getElement(getElement().getRExpression());
    }

    public IReference getVariable() {
        return getElement(getElement().getLExpression());
    }

    @Override
    public String getName() {
        return getVariable().getName();
    }

    @NotNull
    @Override
    public IVariableDeclaration declaration() {
        return getVariable().declaration();
    }
}
