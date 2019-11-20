package streamline.plugin.nodes.guts;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SelfPresentingNode {
    protected final Listeners onUpdate = new Listeners();
    private final Project project;
    private DefaultMutableTreeNode node;
    protected Tree tree;
    private NodeComponent component;
    private NodePanelBuilder panelBuilder = new NodePanelBuilder();

    public SelfPresentingNode(Project project) {
        this.project = project;
    }

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
        return panelBuilder.createPanel();
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

    protected Project getProject() {
        return project;
    }

    @NotNull
    protected Consumer<NodePanel> textRenderer(Presenter presenter) {
        return panel -> {
            TextRenderer text = new TextRenderer(getProject(), presenter);
            onUpdate.listen(text::update); // TODO remove when panel is recreated
            panel.addNodeComponent(text);
        };
    }

    @SafeVarargs
    public final void setNodePanelParts(Consumer<NodePanel>... parts) {
        panelBuilder = new NodePanelBuilder();
        for (Consumer<NodePanel> part : parts) {
            panelBuilder.add(part);
        }
    }

    // TODO maybe just list will be enough?
    public static class NodePanelBuilder {
        private final ArrayList<Consumer<NodePanel>> builders = new ArrayList<>();

        public NodePanelBuilder add(Consumer<NodePanel> part) {
            getParts().add(part);
            return this;
        }

        public ArrayList<Consumer<NodePanel>> getParts() {
            return builders;
        }

        @NotNull
        public NodePanel createPanel() {
            NodePanel nodeComponent = new NodePanel();
            for (Consumer<NodePanel> component : getParts()) {
                component.accept(nodeComponent);
            }
            return nodeComponent;
        }

    }
}
