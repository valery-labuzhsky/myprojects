package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public abstract class SelfPresentingNode extends SimpleNode {
    protected DefaultMutableTreeNode node;
    protected DefaultTreeModel model;
    private NodeComponent component;

    public SelfPresentingNode(Project project) {
        super(project);
    }

    protected NodeComponent createNodeComponent() {
        return new NodeRendererComponent();
    }

    public NodeComponent getNodeComponent() {
        if (component==null) {
            component = createNodeComponent();
        }
        return component;
    }

    @NotNull
    public DefaultMutableTreeNode createTreeNode(DefaultTreeModel model) {
        node = createTreeNode(model, this);
        this.model = model;
        return node;
    }

    @NotNull
    private static DefaultMutableTreeNode createTreeNode(DefaultTreeModel model, SimpleNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SimpleNode child : node.getChildren()) {
            if (child instanceof SelfPresentingNode) {
                treeNode.add(((SelfPresentingNode) child).createTreeNode(model));
            } else {
                treeNode.add(createTreeNode(model, child));
            }
        }
        return treeNode;
    }
}
