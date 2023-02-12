package statref.model.idea.expressions;

import com.intellij.psi.PsiElement;
import statref.model.idea.IVariableDeclaration;

public interface IVariableReference {
    String getName();

    PsiElement getElement();

    IVariableDeclaration declaration();
}
