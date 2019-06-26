package statref.model.idea;

import com.intellij.psi.PsiForStatement;

public class IForStatement extends ILoopStatement<PsiForStatement> {
    public IForStatement(PsiForStatement element) {
        super(element);
    }
}
