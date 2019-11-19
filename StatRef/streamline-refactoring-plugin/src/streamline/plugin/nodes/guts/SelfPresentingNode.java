package streamline.plugin.nodes.guts;

import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.function.Supplier;

public abstract class SelfPresentingNode {
    protected final Listeners onUpdate = new Listeners();
    private DefaultMutableTreeNode node;
    protected Tree tree;
    private NodeComponent component;
    private Supplier<NodeComponent> componentFactory = NodeRendererComponent::new;

    @NotNull
    public DefaultMutableTreeNode createTreeNode(Tree tree) {
        this.tree = tree;
        node = createTreeNode(this);
        return getNode();
    }

    @NotNull
    private DefaultMutableTreeNode createTreeNode(SelfPresentingNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SelfPresentingNode child : node.getChildren()) {
            treeNode.add(child.createTreeNode(getTree()));
        }
        return treeNode;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    public Tree getTree() {
        return tree;
    }

    public SelfPresentingNode findNode(TreePath path) {
        if (path == null || path.getPathCount() == 0) {
            return this;
        } else {
            SelfPresentingNode node = findNode(path.getParentPath());
            if (node != null) {
                Object component = path.getLastPathComponent();
                if (component instanceof DefaultMutableTreeNode) {
                    for (SelfPresentingNode child : node.getChildren()) {
                        if (child.equals(((DefaultMutableTreeNode) component).getUserObject())) {
                            return child;
                        }
                    }
                }
            }
            return null;
        }
    }

    public void select() {
        getTree().setSelectionPath(new TreePath(getNode().getPath()));
    }

    private NodeComponent createNodeComponent() {
        return componentFactory.get();
    }

    public void setComponentFactory(Supplier<NodeComponent> componentFactory) {
        this.componentFactory = componentFactory;
    }

    public NodeComponent getNodeComponent() {
        if (component == null && node != null) {
            component = createNodeComponent();
        }
        return component;
    }

    protected void afterTreeNodeCreated() {
    }

    public abstract List<? extends SelfPresentingNode> getChildren();

    public boolean showRoot() {
        return true;
    }

    private void notifyNodeChanged() {
        if (tree != null) {
            ((DefaultTreeModel) tree.getModel()).nodeChanged(getNode());
        }
    }

    public void update() {
        onUpdate.fire();
        notifyNodeChanged();
    }
}
