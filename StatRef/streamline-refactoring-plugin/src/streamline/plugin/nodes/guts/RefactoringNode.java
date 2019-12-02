package streamline.plugin.nodes.guts;

import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.components.EnabledRefactoringCheckBox;
import streamline.plugin.refactoring.guts.Listeners;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.List;
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
    }

    protected void setNodePanelParts(Presenter presenter) {
        setNodePanelParts(enabledCheckBox(), textRenderer(presenter));
    }

    @Override
    public void afterTreeNodeCreated() {
        getListeners().invoke(() -> {
            TreePath path = new TreePath(getNode().getPath());
            if (getRefactoring().isEnabled()) {
                getTree().expandPath(path);
            } else {
                getTree().collapsePath(path);
            }
        });
        for (int i = 0; i < getNode().getChildCount(); i++) {
            TreeNode node = getNode().getChildAt(i);
            if (node instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
                if (userObject instanceof RefactoringNode) {
                    ((RefactoringNode) userObject).afterTreeNodeCreated();
                }
            }
        }
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> getChildren() {
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

    @NotNull
    protected Consumer<NodePanel> enabledCheckBox() {
        return panel -> {
            EnabledRefactoringCheckBox enabled = new EnabledRefactoringCheckBox(RefactoringNode.this);
            panel.add(enabled);
            panel.dispatchKeyEvents(enabled);
        };
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
}
