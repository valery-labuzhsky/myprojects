package streamline.plugin.nodes.guts;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.function.Supplier;

public abstract class SelfPresentingNode {
    private DefaultMutableTreeNode node;
    private Tree tree;
    private NodeComponent component;
    private Presenter presenter;

    private final MySimpleNode nodeDescriptor;

    private Supplier<NodeComponent> componentFactory = NodeRendererComponent::new;

    public SelfPresentingNode(Project project) {
        nodeDescriptor = new MySimpleNode(project);
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

    public abstract SelfPresentingNode[] getChildren();

    @NotNull
    private DefaultMutableTreeNode createTreeNode(SelfPresentingNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SelfPresentingNode child : node.getChildren()) {
            treeNode.add(child.createTreeNode(getTree()));
        }
        return treeNode;
    }

    protected void notifyNodeChanged() {
        if (tree != null) {
            ((DefaultTreeModel) tree.getModel()).nodeChanged(getNode());
        }
    }

    protected Presenter getPresenter() {
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
                SelfPresentingNode[] children = node.getChildren();
                if (component instanceof DefaultMutableTreeNode) {
                    for (SelfPresentingNode child : children) {
                        if (child.equals(((DefaultMutableTreeNode) component).getUserObject())) {
                            return child;
                        }
                    }
                }
            }
            return null;
        }
    }

    public void update() {
        nodeDescriptor.update();
    }

    public Project getProject() {
        return nodeDescriptor.getProject();
    }

    public MySimpleNode getNodeDescriptor() {
        return nodeDescriptor;
    }

    // TODO rename me
    // TODO use PresentableNodeDescriptor
    private class MySimpleNode extends SimpleNode {
        public MySimpleNode(Project project) {
            super(project);
        }

        @NotNull
        @Override
        public SimpleNode[] getChildren() {
            return new SimpleNode[0];
        }

        @Override
        protected void doUpdate() {
            getPresenter().update(nodeDescriptor.getTemplatePresentation());
            SwingUtilities.invokeLater(SelfPresentingNode.this::notifyNodeChanged);
        }

    }
}
