package statref.model.idea.expressions;

import com.intellij.psi.PsiReferenceExpression;
import statref.model.expressions.SLocalVariable;
import statref.model.idea.ILocalVariableDeclaration;

public class ILocalVariable extends IReference implements SLocalVariable {

    public ILocalVariable(PsiReferenceExpression element) {
        super(element);
    }

    @Override
    public ILocalVariableDeclaration declaration() {
        return (ILocalVariableDeclaration) super.declaration();
    }

}
