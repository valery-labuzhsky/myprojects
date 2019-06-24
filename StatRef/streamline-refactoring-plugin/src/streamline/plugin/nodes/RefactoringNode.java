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

public abstract class RefactoringNode<R extends Refactoring> extends SelfPresentingNode {
    protected final R refactoring;
    protected final NodesRegistry registry;
    private final Listeners listeners;
    private SimpleNode[] children;

    public RefactoringNode(Project project, R refactoring, NodesRegistry registry) {
        super(project);
        this.refactoring = refactoring;
        this.registry = registry;
        listeners = registry.getListeners(refactoring);
        getListeners().add(this::update);
        setComponentFactory(() -> new CheckBoxEnabledPanel(this));
    }

    public static RefactoringNode create(Project project, Refactoring refactoring, NodesRegistry registry) {
        if (refactoring instanceof InlineUsage) {
            return new InlineUsageNode(project, (InlineUsage) refactoring, registry);
        } else if (refactoring instanceof InlineAssignment) {
            return new AssignmentNode(project, (InlineAssignment) refactoring, registry);
        } else if (refactoring instanceof CompoundRefactoring) {
            return new CompoundNode(project, (CompoundRefactoring) refactoring, registry);
        } else {
            throw new IllegalArgumentException("Unknown refactoring " + refactoring.getClass().getSimpleName());
        }
    }

    @Override
    public void afterTreeNodeCreated() {
        getListeners().add(() -> {
            TreePath path = new TreePath(getNode().getPath());
            if (getRefactoring().isEnabled()) {
                getTree().expandPath(path);
            } else {
                getTree().collapsePath(path);
            }
        });
        for (SimpleNode child : getChildren()) {
            if (child instanceof RefactoringNode) {
                ((RefactoringNode) child).afterTreeNodeCreated();
            }
        }
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

    public Listeners getListeners() {
        return listeners;
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
                    node.getListeners().fire();
                }
            });
            node.getListeners().add(() ->
                    checkBox.setSelected(node.getRefactoring().isEnabled()));
            return checkBox;
        }

    }

    public class RefactoringPresenter extends ElementPresenter {
        public RefactoringPresenter(String prefix, PsiElement psiElement) {
            super(prefix, psiElement);
        }

        @Override
        protected SimpleTextAttributes update(SimpleTextAttributes attributes) {
            if (!refactoring.isEnabled()) {
                return SimpleTextAttributes.merge(attributes, SimpleTextAttributes.GRAYED_ATTRIBUTES);
            } else {
                return attributes;
            }
        }
    }

    public void setEnabled(boolean enabled) {
        if (refactoring.setEnabled(enabled)) {
            getListeners().fire();
        }
    }

    @NotNull
    public abstract SimpleNode[] createChildren();

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{refactoring};
    }
}
