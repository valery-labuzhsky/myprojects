package streamline.plugin.nodes.guts;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;

public class ElementPresenter extends SimplePresenter {
    private final PsiElement psiElement;

    public ElementPresenter(String prefix, PsiElement psiElement) {
        this.psiElement = psiElement;

        italic().add(prefix).add(getContext()).bold().inline(getMainElement());
    }

    private PsiElement getMainElement() {
        return this.psiElement;
    }

    private PsiElement getContext() {
        PsiElement parent = psiElement;
        while (parent != null && !(parent.getParent() instanceof PsiStatement)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            return psiElement.getParent();
        }
        return parent;
    }

}
