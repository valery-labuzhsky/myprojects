package statref.model.idea;

import com.intellij.psi.PsiDeclarationStatement;

public class IDeclarationStatement extends IStatement<PsiDeclarationStatement> {
    public IDeclarationStatement(PsiDeclarationStatement declaration) {
        super(declaration);
    }
}