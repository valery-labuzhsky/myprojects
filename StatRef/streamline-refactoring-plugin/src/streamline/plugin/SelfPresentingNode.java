package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public abstract class SelfPresentingNode extends SimpleNode {
    protected DefaultMutableTreeNode node;
    protected Tree tree;
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
    public DefaultMutableTreeNode createTreeNode(Tree tree) {
        this.tree = tree;
        node = createTreeNode(this);
        return node;
    }

    @NotNull
    private DefaultMutableTreeNode createTreeNode(SimpleNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (SimpleNode child : node.getChildren()) {
            if (child instanceof SelfPresentingNode) {
                treeNode.add(((SelfPresentingNode) child).createTreeNode(tree));
            } else {
                treeNode.add(createTreeNode(child));
            }
        }
        return treeNode;
    }

    protected DefaultTreeModel getModel() {
        return (DefaultTreeModel) this.tree.getModel();
    }

    protected void fireNodeChanged() {
        getModel().nodeChanged(node);
    }
}
