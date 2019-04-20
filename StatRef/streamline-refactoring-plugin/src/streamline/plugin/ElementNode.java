package streamline.plugin;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class ElementNode extends SimpleNode {
    public ElementNode(Project project) {
        super(project);
    }

    @NotNull
    public DefaultMutableTreeNode createTreeNode() {
        return createTreeNode(this);
    }

    @NotNull
    public DefaultMutableTreeNode createTreeNode(SimpleNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SimpleNode child : node.getChildren()) {
            treeNode.add(createTreeNode(child));
        }
        return treeNode;
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
        presentation.addText(statementText.substring(0, elementStart - statementStart), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        presentation.addText(getPsiElement().getText(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        presentation.addText(statementText.substring(elementStart - statementStart + getPsiElement().getTextLength()), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
}
