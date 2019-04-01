package statref.model.idea;

import com.intellij.psi.PsiBlockStatement;

public class IBlockStatement extends IStatement<PsiBlockStatement> {
    public IBlockStatement(PsiBlockStatement statement) {
        super(statement);
    }
}
