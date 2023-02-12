package streamline.plugin.nodes.guts;

import org.apache.commons.collections.iterators.IteratorEnumeration;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractTreeNode<C extends AbstractTreeNode> implements TreeNode {
    private TreeNode parent;

    public abstract List<C> getChildren();

    @Override
    public C getChildAt(int childIndex) {
        return getChildren().get(childIndex);
    }

    @Override
    public int getChildCount() {
        return getChildren().size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return getChildren().indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @Override
    public Enumeration children() {
        return new IteratorEnumeration(getChildren().iterator());
    }

    public TreePath getPath() {
        return getPath(this);
    }

    private TreePath getPath(TreeNode node) {
        TreeNode parent = node.getParent();
        if (parent == null) {
            return new TreePath(node);
        } else {
            return new TreePath(getPath(parent), node) {
            };
        }
    }
}
