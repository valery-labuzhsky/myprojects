package statref.model.idea;

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;

public class IAssignment extends IExpression<PsiAssignmentExpression> implements IInitializer {
    public IAssignment(PsiAssignmentExpression assignment) {
        super(assignment);
    }

    @Override
    public IExpression getInitializer() {
        return IFactory.getElement(getElement().getRExpression());
    }

    public IVariable getVariable() {
        return IFactory.getElement((PsiReferenceExpression) getElement().getLExpression());
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
