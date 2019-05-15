package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.NodeComponent;
import streamline.plugin.nodes.NodePanel;
import streamline.plugin.nodes.RefactoringNode;

import javax.swing.*;
import javax.swing.tree.TreePath;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    private JCheckBox checkBox;

    public InlineUsageNode(Project project, InlineUsage refactoring) {
        super(project, refactoring);
    }

    @Override
    protected NodeComponent createNodeComponent() {
        checkBox = new JCheckBox();
        checkBox.setSelected(refactoring.isEnabled());
        checkBox.addActionListener((e) -> {
            refactoring.setEnabled(checkBox.isSelected());
            update();
            fireNodeChanged();
            // TODO make initial state accordingly
            TreePath path = new TreePath(node.getPath());
            if (checkBox.isSelected()) {
                tree.expandPath(path);
            } else {
                tree.collapsePath(path);
            }
        });
        return new NodePanel(checkBox);
    }

    @Override
    protected SimpleTextAttributes getElementAttributes() {
        if (refactoring.isEnabled()) {
            return SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        } else {
            return SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES;
        }
    }

    @Override
    protected SimpleTextAttributes getStatementAttributes() {
        if (refactoring.isEnabled()) {
            return SimpleTextAttributes.REGULAR_ATTRIBUTES;
        } else {
            return SimpleTextAttributes.GRAYED_ATTRIBUTES;
        }
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        return new SimpleNode[]{new VariantsNode(myProject, refactoring)};
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getUsage().getElement();
    }
}
