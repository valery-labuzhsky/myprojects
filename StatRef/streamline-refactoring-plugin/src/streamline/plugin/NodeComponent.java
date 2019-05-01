package streamline.plugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;

public abstract class NodeComponent {
    @NotNull
    public TreeCellRenderer createRenderer() {
        return (tree, value, selected, expanded, leaf, row, hasFocus) -> {
            prepare(tree, value, selected, expanded, leaf, row, hasFocus);
            return getComponent();
        };
    }

    protected abstract JComponent getComponent();

    protected abstract void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

}
