package statref.model.idea;

import com.intellij.psi.*;

public abstract class ILoopStatement<L extends PsiLoopStatement> extends IStatement<L> {
    public ILoopStatement(L element) {
        super(element);
    }

    public IStatement getBody() {
        return (IStatement) IFactory.getElement(getElement().getBody());
    }

}
