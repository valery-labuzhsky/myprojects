package statref.model.idea;

import com.intellij.psi.PsiAssignmentExpression;
import statref.model.SElement;
import statref.model.SInitializer;

public class IAssignment extends IExpression<PsiAssignmentExpression> implements SInitializer {
    public IAssignment(PsiAssignmentExpression assignment) {
        super(assignment);
    }

    @Override
    public IExpression getInitializer() {
        return IFactory.getExpression(getElement().getRExpression());
    }

    @Override
    public SElement getVariable() {
        return IFactory.getExpression(getElement().getLExpression());
    }
}
