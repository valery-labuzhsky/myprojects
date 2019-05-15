package streamline.plugin.nodes;

import com.intellij.ide.util.treeView.NodeRenderer;

import javax.swing.*;

public class NodeRendererComponent extends NodeComponent {
    private final NodeRenderer renderer = new NodeRenderer();

    @Override
    public JComponent getComponent() {
        return renderer;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
