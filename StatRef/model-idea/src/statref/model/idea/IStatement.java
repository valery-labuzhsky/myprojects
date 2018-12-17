package statref.model.idea;

import com.intellij.psi.PsiStatement;

public class IStatement<PSI extends PsiStatement> extends IElement<PSI> {
    public IStatement(PSI element) {
        super(element);
    }
}
