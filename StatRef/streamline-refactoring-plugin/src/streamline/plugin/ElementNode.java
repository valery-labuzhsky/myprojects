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
import javax.swing.tree.DefaultTreeModel;

public abstract class ElementNode extends SelfPresentingNode {

    protected DefaultMutableTreeNode node;
    private DefaultMutableTreeNode treeNode;

    public ElementNode(Project project) {
        super(project);
    }

    @NotNull
    public DefaultMutableTreeNode createTreeNode(DefaultTreeModel model) {
        return createTreeNode(model, this);
    }

    @NotNull
    public DefaultMutableTreeNode createTreeNode(DefaultTreeModel model, SimpleNode node) {
        treeNode = new DefaultMutableTreeNode(node);
        for (SimpleNode child : node.getChildren()) {
            // TODO pass model further
            treeNode.add(createTreeNode(model, child));
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
