package statref.model.idea.expressions;

import com.intellij.psi.PsiReferenceExpression;
import statref.model.expressions.SParameter;
import statref.model.idea.IParameterDeclaration;

/**
 * Created on 29.11.2020.
 *
 * @author unicorn
 */
public class IParameter extends IReference implements SParameter {
    public IParameter(PsiReferenceExpression expression) {
        super(expression);
    }

    @Override
    public IParameterDeclaration declaration() {
        return (IParameterDeclaration) super.declaration();
    }
}
