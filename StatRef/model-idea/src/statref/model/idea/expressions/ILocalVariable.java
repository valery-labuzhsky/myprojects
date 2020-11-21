package statref.model.idea.expressions;

import com.intellij.psi.PsiReferenceExpression;
import statref.model.expressions.SExpression;
import statref.model.expressions.SLocalVariable;
import statref.model.fragment.Place;
import statref.model.idea.ILocalVariableDeclaration;

import java.util.Collections;
import java.util.List;

public class ILocalVariable extends IReference implements SLocalVariable {

    public ILocalVariable(PsiReferenceExpression element) {
        super(element);
    }

    @Override
    public ILocalVariableDeclaration declaration() {
        return (ILocalVariableDeclaration) super.declaration();
    }

    @Override
    public List<Place<SExpression>> getExpressions() {
        return Collections.emptyList();
    }

    @Override
    public Object getSignature() {
        return this;
    }
}
