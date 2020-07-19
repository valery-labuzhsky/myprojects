package statref.model.idea;

import com.intellij.psi.PsiBinaryExpression;

public class IBinaryExpression extends IExpression {
    public IBinaryExpression(PsiBinaryExpression expression) {
        super(expression);
    }
}
