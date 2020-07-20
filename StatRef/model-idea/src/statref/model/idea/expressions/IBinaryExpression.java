package statref.model.idea.expressions;

import com.intellij.psi.PsiBinaryExpression;

public class IBinaryExpression extends IExpression {
    public IBinaryExpression(PsiBinaryExpression expression) {
        super(expression);
    }
}
