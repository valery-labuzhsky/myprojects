package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.util.EventObject;

public abstract class NodeComponent {
    public boolean isEditable(EventObject e) {
        return false;
    }

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
