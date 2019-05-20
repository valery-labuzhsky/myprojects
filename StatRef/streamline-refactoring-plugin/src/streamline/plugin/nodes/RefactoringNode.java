package streamline.plugin.nodes;

import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.Refactoring;

import javax.swing.*;
import javax.swing.tree.TreePath;

public abstract class RefactoringNode<R extends Refactoring> extends ElementNode {
    protected final R refactoring;
    private SimpleNode[] children;
    private JCheckBox checkBox;

    public RefactoringNode(Project project, R refactoring, String prefix) {
        super(project, prefix);
        this.refactoring = refactoring;
        update();
    }

    @Override
    public SimpleNode[] getChildren() {
        if (children == null) {
            children = createChildren();
        }
        return children;
    }

    public R getRefactoring() {
        return refactoring;
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
    public abstract SimpleNode[] createChildren();
}
