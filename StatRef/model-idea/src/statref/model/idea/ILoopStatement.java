package statref.model.idea;

import com.intellij.psi.PsiDoWhileStatement;
import com.intellij.psi.PsiForStatement;
import com.intellij.psi.PsiForeachStatement;
import com.intellij.psi.PsiLoopStatement;

public abstract class ILoopStatement<L extends PsiLoopStatement> extends IStatement<L> {
    public ILoopStatement(L element) {
        super(element);
    }

    public IStatement getBody() {
        return (IStatement) IFactory.getStatement(getElement().getBody());
    }

}
