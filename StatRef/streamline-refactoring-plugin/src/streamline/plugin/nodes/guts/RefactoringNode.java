package streamline.plugin.nodes.guts;

import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.components.EnabledRefactoringCheckBox;
import streamline.plugin.refactoring.guts.Listeners;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class RefactoringNode<R extends Refactoring> extends SelfPresentingNode {
    protected final R refactoring;
    protected final NodesRegistry registry;
    private final Listeners listeners;
    private List<SelfPresentingNode> children;

    public RefactoringNode(R refactoring, NodesRegistry registry) {
        super(registry.getProject());
        this.refactoring = refactoring;
        this.registry = registry;
        listeners = registry.getListeners(refactoring);
        getListeners().invoke(this::update);
        refactoring.onUpdate.listen(() -> getListeners().fire()); // TODO get rid of node listeners eventually?
    }

    protected void setNodePanelParts(Presenter presenter) {
        setNodePanelParts(enabledCheckBox(), textRenderer(presenter));
    }

    @Override
    public void afterTreeNodeCreated() {
        getListeners().invoke(() -> {
            TreePath path = getPath();
            TreePath parent = path.getParentPath();
            if (parent == null || getTree().isExpanded(parent)) {
                if (getRefactoring().isEnabled()) {
                    getTree().expandPath(path);
                } else {
                    getTree().collapsePath(path);
                }
            }
        });
        for (SelfPresentingNode child : getChildren()) {
            child.afterTreeNodeCreated();
        }
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>(createChildren());
            for (SelfPresentingNode child : children) {
                child.setParent(this);
            }
        }
        return children;
    }

    public R getRefactoring() {
        return refactoring;
    }

    public Listeners getListeners() {
        return listeners;
    }

    @NotNull
    protected Consumer<NodePanel> enabledCheckBox() {
        return panel -> {
            EnabledRefactoringCheckBox enabled = new EnabledRefactoringCheckBox(RefactoringNode.this);
            panel.add(enabled);
            panel.dispatchKeyEvents(enabled);
        };
    }

    public RefactoringNode select(Refactoring selected) {
        if (refactoring.equals(selected)) {
            select();
            return this;
        } else {
            for (SelfPresentingNode child : getChildren()) {
                if (child instanceof RefactoringNode) {
                    RefactoringNode node = ((RefactoringNode) child).select(selected);
                    if (node != null) {
                        return node;
                    }
                }
            }
        }
        return null;
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
    public List<SelfPresentingNode> createChildren() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefactoringNode<?> that = (RefactoringNode<?>) o;
        return refactoring.equals(that.refactoring);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refactoring);
    }
}
