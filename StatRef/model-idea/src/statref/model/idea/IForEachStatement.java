package statref.model.idea;

import com.intellij.psi.PsiForeachStatement;

public class IForEachStatement extends ILoopStatement<PsiForeachStatement> {
    public IForEachStatement(PsiForeachStatement element) {
        super(element);
    }
}
