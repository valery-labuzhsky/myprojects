package streamline.plugin.nodes.guts;

import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.components.EnabledRefactoringCheckBox;
import streamline.plugin.refactoring.guts.Listeners;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.Objects;

public abstract class RefactoringNode<R extends Refactoring> extends SelfPresentingNode {
    protected final R refactoring;
    protected final NodesRegistry registry;
    private final Listeners listeners;
    private SelfPresentingNode[] children;

    public RefactoringNode(R refactoring, NodesRegistry registry) {
        super(registry.getProject());
        this.refactoring = refactoring;
        this.registry = registry;
        listeners = registry.getListeners(refactoring);
        getListeners().add(this::update);
        setComponentFactory(() -> new CheckBoxEnabledPanel(this));
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
        for (SelfPresentingNode child : getChildren()) {
            child.afterTreeNodeCreated();
        }
    }

    @NotNull
    @Override
    public SelfPresentingNode[] getChildren() {
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
            return new EnabledRefactoringCheckBox(node);
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
    public SelfPresentingNode[] createChildren() {
        return new SelfPresentingNode[0];
    }

    // TODO why?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefactoringNode<?> that = (RefactoringNode<?>) o;
        return refactoring.equals(that.refactoring);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refactoring);
    }
}
