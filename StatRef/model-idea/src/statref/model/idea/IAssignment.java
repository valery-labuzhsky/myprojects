package statref.model.idea;

import com.intellij.psi.PsiAssignmentExpression;

public class IAssignment extends IExpression<PsiAssignmentExpression> implements statref.model.SInitializer {
    public IAssignment(PsiAssignmentExpression assignment) {
        super(assignment);
    }

    @Override
    public IExpression getInitializer() {
        return IFactory.getExpression(getElement().getRExpression());
    }
}
