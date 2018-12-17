package statref.model.idea;

import com.intellij.psi.PsiExpression;
import statref.model.expression.SExpression;

public abstract class IExpression<PSI extends PsiExpression> extends IElement<PSI> implements SExpression {
    public IExpression(PSI expression) {
        super(expression);
    }

}
