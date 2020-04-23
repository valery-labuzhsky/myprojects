package streamline.plugin.nodes.guts;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class ElementPresenter extends SimplePresenter {
    private final PsiElement psiElement;

    public ElementPresenter(String prefix, PsiElement psiElement) {
        this.psiElement = psiElement;

        italic().add(prefix).add(getContext()).bold().inline(getMainElement());
    }

    private PsiElement getMainElement() {
        return this.psiElement;
    }

    @Nullable
    private PsiElement getContext() {
        // TODO statement is too much,
        // TODO I need to look at the tree!
        // TODO I need psi viewer plugin
        return PsiTreeUtil.getParentOfType(psiElement, PsiStatement.class, false);
    }

}
