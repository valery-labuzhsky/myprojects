package statref.model.idea;

import com.intellij.psi.PsiField;
import statref.model.members.SFieldDeclaration;

/**
 * Created on 21.07.2020.
 *
 * @author unicorn
 */
public class IFieldDeclaration extends IVariableDeclaration implements SFieldDeclaration, IInitializer {
    public IFieldDeclaration(PsiField element) {
        super(element);
    }

    @Override
    public PsiField getElement() {
        return (PsiField) super.getElement();
    }

    @Override
    public IFieldDeclaration declaration() {
        return this;
    }
}
