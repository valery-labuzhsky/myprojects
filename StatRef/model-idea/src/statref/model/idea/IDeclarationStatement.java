package statref.model.idea;

import com.intellij.psi.PsiDeclarationStatement;

public class IDeclarationStatement extends IStatement {
    public IDeclarationStatement(PsiDeclarationStatement declaration) {
        super(declaration);
    }
}