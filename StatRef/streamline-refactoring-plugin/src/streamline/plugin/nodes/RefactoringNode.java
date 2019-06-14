package streamline.plugin.nodes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.Listeners;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.assignment.AssignmentNode;
import streamline.plugin.refactoring.assignment.InlineAssignment;
import streamline.plugin.refactoring.compound.CompoundNode;
import streamline.plugin.refactoring.compound.CompoundRefactoring;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.function.Consumer;

public abstract class RefactoringNode<R extends Refactoring> extends SelfPresentingNode {
    protected final R refactoring;
    private SimpleNode[] children;
    private boolean intervened;

    public RefactoringNode(Project project, R refactoring) {
        super(project);
        this.refactoring = refactoring;
        getListeners().addListener(this::update);
        setComponentFactory(() -> new CheckBoxEnabledPanel(this));
    }

    public static RefactoringNode create(Project project, Refactoring refactoring) {
        if (refactoring instanceof InlineUsage) {
            return new InlineUsageNode(project, (InlineUsage) refactoring);
        } else if (refactoring instanceof InlineAssignment) {
            return new AssignmentNode(project, (InlineAssignment) refactoring);
        } else if (refactoring instanceof CompoundRefactoring) {
            return new CompoundNode(project, (CompoundRefactoring) refactoring);
        } else {
            throw new IllegalArgumentException("Unknown refactoring "+refactoring.getClass().getSimpleName());
        }
    }

    @Override
    protected void afterTreeNodeCreated() {
        getListeners().addListener(() -> {
            TreePath path = new TreePath(getNode().getPath());
            if (getRefactoring().isEnabled()) {
                getTree().expandPath(path);
            } else {
                getTree().collapsePath(path);
            }
        });
    }

    @NotNull
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

    public boolean isIntervened() {
        return intervened;
    }

    public void setIntervened(boolean intervened) {
        this.intervened = intervened;
    }

    public static class CheckBoxEnabledPanel extends NodePanel<JCheckBox> {
        public CheckBoxEnabledPanel(RefactoringNode node) {
            super(createCheckBox(node));
        }

        @NotNull
        private static JCheckBox createCheckBox(RefactoringNode node) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener((e) -> {
                if (node.getRefactoring().setEnabled(checkBox.isSelected())) {
                    node.setIntervened(true);
                    node.getListeners().fireRefactoringChanged();
                }
            });
            node.getListeners().addListener(() ->
                    checkBox.setSelected(node.getRefactoring().isEnabled()));
            return checkBox;
        }

    }

    public class RefactoringPresenter extends ElementPresenter {
        public RefactoringPresenter(String prefix, PsiElement psiElement) {
            super(prefix, psiElement);
        }

        @Override
        protected SimpleTextAttributes getPrefixAttributes() {
            // TODO try to merge them
            if (refactoring.isEnabled()) {
                return SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES;
            } else {
                return SimpleTextAttributes.merge(SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES,
                        SimpleTextAttributes.GRAYED_ATTRIBUTES);
            }
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

    }

    public void addMutationListener(Consumer<Refactoring> consumer) {
    }

    @NotNull
    public abstract SimpleNode[] createChildren();

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{refactoring};
    }
}
