package streamline.plugin.nodes.guts;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.*;

public class NodeRendererComponent extends NodeComponent {
    private final NodeRenderer renderer = new NodeRenderer();

    @Override
    public JComponent getComponent() {
        return renderer;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // TODO push me up the usage tree
        Object userObject = TreeUtil.getUserObject(value);
        if (userObject instanceof SelfPresentingNode) {
            userObject = ((SelfPresentingNode) userObject).getNodeDescriptor();
        }
        renderer.getTreeCellRendererComponent(tree, userObject, selected, expanded, leaf, row, hasFocus);
    }
}
