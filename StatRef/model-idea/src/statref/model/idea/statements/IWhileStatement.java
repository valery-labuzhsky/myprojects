package statref.model.idea.statements;

import com.intellij.psi.PsiWhileStatement;

public class IWhileStatement extends ILoopStatement<PsiWhileStatement> {

    public IWhileStatement(PsiWhileStatement element) {
        super(element);
    }
}
