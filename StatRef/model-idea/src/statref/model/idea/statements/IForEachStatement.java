package statref.model.idea.statements;

import com.intellij.psi.PsiForeachStatement;

public class IForEachStatement extends ILoopStatement<PsiForeachStatement> {
    public IForEachStatement(PsiForeachStatement element) {
        super(element);
    }
}
