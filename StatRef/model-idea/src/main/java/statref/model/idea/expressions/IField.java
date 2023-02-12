package statref.model.idea.expressions;

import com.intellij.psi.PsiReferenceExpression;
import statref.model.expressions.SField;
import statref.model.idea.IFieldDeclaration;
import statref.model.types.SType;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public class IField extends IReference implements SField {
    public IField(PsiReferenceExpression expression) {
        super(expression);
    }

    @Override
    public SType getQualifier() {
        return null;
    }

    @Override
    public IFieldDeclaration declaration() {
        return (IFieldDeclaration) super.declaration();
    }
}
