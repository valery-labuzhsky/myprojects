package statref.model.idea;

import com.intellij.psi.PsiBinaryExpression;

public class IBinaryExpression extends IExpression<PsiBinaryExpression> {
    public IBinaryExpression(PsiBinaryExpression expression) {
        super(expression);
    }
}
