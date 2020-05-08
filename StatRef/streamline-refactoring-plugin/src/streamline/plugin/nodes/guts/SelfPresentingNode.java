package streamline.plugin.nodes.guts;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.guts.Listeners;
import streamline.plugin.tree.Monkey;
import streamline.plugin.tree.NodeTreeterator;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SelfPresentingNode extends AbstractTreeNode<SelfPresentingNode> {
    protected final Listeners onUpdate = new Listeners();
    private final Project project;
    protected Tree tree;
    private NodeComponent component;
    private NodePanelBuilder panelBuilder = new NodePanelBuilder();

    public SelfPresentingNode(Project project) {
        this.project = project;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
        for (SelfPresentingNode child : getChildren()) {
            child.setTree(tree);
        }
    }

    @Override
    public abstract List<SelfPresentingNode> getChildren();

    public void setChildren(ArrayList<SelfPresentingNode> children) {
        getChildren().clear();
        getChildren().addAll(children);
    }

    public LinkedHashMap<Integer, SelfPresentingNode> filterChildren(Predicate<SelfPresentingNode> predicate) {
        LinkedHashMap<Integer, SelfPresentingNode> removed = new LinkedHashMap<>();
        ArrayList<SelfPresentingNode> filtered = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            SelfPresentingNode child = getChildAt(i);
            if (predicate.test(child)) {
                filtered.add(child);
            } else {
                removed.put(i, child);
            }
        }
        if (filtered.size() != getChildren().size()) {
            setChildren(filtered);
        }
        return removed;
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
                for (SelfPresentingNode child : node.getChildren()) {
                    if (child.equals(component)) {
                        return child;
                    }
                }
            }
            return null;
        }
    }

    public void select() {
        getTree().setSelectionPath(getPath());
    }

    private NodeComponent createNodeComponent() {
        return panelBuilder.createPanel();
    }

    public NodeComponent getNodeComponent() {
        if (component == null) {
            component = createNodeComponent();
        }
        return component;
    }

    protected void afterTreeNodeCreated() {
    }

    public boolean showRoot() {
        return true;
    }

    private void notifyNodeChanged() {
        if (tree != null) {
            ((DefaultTreeModel) tree.getModel()).nodeChanged(this);
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

    public static class SelfPresentingTreeterator extends NodeTreeterator<SelfPresentingNode> {
        public SelfPresentingTreeterator(SelfPresentingNode node) {
            super(node);
        }

        @Override
        protected boolean isLeaf(SelfPresentingNode node) {
            return node.isLeaf();
        }

        @Override
        @NotNull
        protected Iterator<SelfPresentingNode> children(SelfPresentingNode node) {
            return node.getChildren().iterator();
        }
    }

    public Monkey<SelfPresentingNode> monkey() {
        return new Monkey<>(new SelfPresentingTreeterator(this));
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
