package statref.model.idea.statements;

import com.intellij.psi.PsiForStatement;

public class IForStatement extends ILoopStatement<PsiForStatement> {
    public IForStatement(PsiForStatement element) {
        super(element);
    }
}
