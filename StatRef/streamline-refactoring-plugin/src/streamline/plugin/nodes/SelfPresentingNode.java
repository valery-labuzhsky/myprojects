package streamline.plugin.nodes;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.Listeners;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.function.Supplier;

public abstract class SelfPresentingNode extends SimpleNode {
    private DefaultMutableTreeNode node;
    private Tree tree;
    private NodeComponent component;
    private Presenter presenter;

    private Supplier<NodeComponent> componentFactory = NodeRendererComponent::new;

    public SelfPresentingNode(Project project) {
        super(project);
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

    @NotNull
    public DefaultMutableTreeNode createTreeNode(Tree tree) {
        this.tree = tree;
        node = createTreeNode(this);
        return getNode();
    }

    protected void afterTreeNodeCreated() {
    }

    @NotNull
    private DefaultMutableTreeNode createTreeNode(SimpleNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SimpleNode child : node.getChildren()) {
            if (child instanceof SelfPresentingNode) {
                treeNode.add(((SelfPresentingNode) child).createTreeNode(getTree()));
            } else {
                treeNode.add(createTreeNode(child));
            }
        }
        return treeNode;
    }

    @Override
    protected void doUpdate() {
        getPresenter().update(getTemplatePresentation());
        SwingUtilities.invokeLater(this::notifyNodeChanged);
    }

    private void notifyNodeChanged() {
        if (tree != null) {
            ((DefaultTreeModel) tree.getModel()).nodeChanged(getNode());
        }
    }

    private Presenter getPresenter() {
        if (presenter == null) {
            presenter = createPresenter();
        }
        return presenter;
    }

    protected abstract Presenter createPresenter();

    public boolean showRoot() {
        return true;
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
                SimpleNode[] children = node.getChildren();
                if (component instanceof DefaultMutableTreeNode) {
                    for (SimpleNode child : children) {
                        if (child.equals(((DefaultMutableTreeNode) component).getUserObject())) {
                            return (SelfPresentingNode) child;
                        }
                    }
                }
            }
            return null;
        }
    }
}
