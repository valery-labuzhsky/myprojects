package streamline.plugin.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.SimpleTextAttributes;

public class ElementPresenter implements Presenter {
    private final String prefix;
    private final PsiElement psiElement;

    public ElementPresenter(String prefix, PsiElement psiElement) {
        this.prefix = prefix;
        this.psiElement = psiElement;
    }

    @Override
    public void update(PresentationData presentation) {
        presentation.clearText();
        PsiStatement statement = PsiTreeUtil.getParentOfType(psiElement, PsiStatement.class, false);
        String statementText = statement.getText();
        int statementStart = statement.getTextRange().getStartOffset();
        int elementStart = psiElement.getTextRange().getStartOffset();
        presentation.addText(prefix, getPrefixAttributes());
        presentation.addText(statementText.substring(0, elementStart - statementStart), getStatementAttributes());
        presentation.addText(psiElement.getText(), getElementAttributes());
        presentation.addText(statementText.substring(elementStart - statementStart + psiElement.getTextLength()), getStatementAttributes());
    }

    protected SimpleTextAttributes getPrefixAttributes() {
        return SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES;
    }

    protected SimpleTextAttributes getElementAttributes() {
        return SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
    }

    protected SimpleTextAttributes getStatementAttributes() {
        return SimpleTextAttributes.REGULAR_ATTRIBUTES;
    }

}
