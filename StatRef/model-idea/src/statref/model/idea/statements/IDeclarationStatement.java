package statref.model.idea.statements;

import com.intellij.psi.PsiDeclarationStatement;

public class IDeclarationStatement extends IStatement {
    public IDeclarationStatement(PsiDeclarationStatement declaration) {
        super(declaration);
    }
}