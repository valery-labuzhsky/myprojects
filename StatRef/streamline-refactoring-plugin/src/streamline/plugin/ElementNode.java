package streamline.plugin;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.SimpleTextAttributes;

public abstract class ElementNode extends SelfPresentingNode {

    public ElementNode(Project project) {
        super(project);
    }

    protected abstract PsiElement getPsiElement();

    @Override
    protected void doUpdate() {
        PresentationData presentation = getTemplatePresentation();
        presentation.clearText();
        PsiStatement statement = PsiTreeUtil.getParentOfType(getPsiElement(), PsiStatement.class);
        String statementText = statement.getText();
        int statementStart = statement.getTextOffset();
        int elementStart = getPsiElement().getTextOffset();
        presentation.addText(statementText.substring(0, elementStart - statementStart), getStatementAttributes());
        presentation.addText(getPsiElement().getText(), getElementAttributes());
        presentation.addText(statementText.substring(elementStart - statementStart + getPsiElement().getTextLength()), getStatementAttributes());
    }

    protected SimpleTextAttributes getElementAttributes() {
        return SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
    }

    protected SimpleTextAttributes getStatementAttributes() {
        return SimpleTextAttributes.REGULAR_ATTRIBUTES;
    }

}
