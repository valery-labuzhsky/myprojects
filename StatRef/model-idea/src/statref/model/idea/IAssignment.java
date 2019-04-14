package statref.model.idea;

import com.intellij.psi.PsiAssignmentExpression;
import statref.model.SElement;

public class IAssignment extends IExpression<PsiAssignmentExpression> implements IInitializer {
    public IAssignment(PsiAssignmentExpression assignment) {
        super(assignment);
    }

    @Override
    public IExpression getInitializer() {
        return IFactory.getExpression(getElement().getRExpression());
    }

    public SElement getVariable() {
        return IFactory.getExpression(getElement().getLExpression());
    }

}
