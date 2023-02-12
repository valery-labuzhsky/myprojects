package statref.model.idea.statements;

import com.intellij.psi.PsiForeachStatement;
import statref.model.idea.IFactory;
import statref.model.idea.expressions.IExpression;

public class IForEachStatement extends ILoopStatement {
    public IForEachStatement(PsiForeachStatement element) {
        super(element);
    }

    public IExpression getIteratedValue() {
        return IFactory.getElement(getElement().getIteratedValue());
    }

    @Override
    public PsiForeachStatement getElement() {
        return (PsiForeachStatement) super.getElement();
    }
}
