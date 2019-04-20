package streamline.plugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.EventObject;

public abstract class NodeComponent {
    @NotNull
    public TreeCellEditor createEditor() {
        return new NodeCellEditor();
    }

    @NotNull
    public TreeCellRenderer createRenderer() {
        // TODO now I must improve it to choose Panel depending on what value we have
        return new TreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                prepare(tree, value, selected, expanded, leaf, row, hasFocus);
                return getComponent();
            }
        };
    }

    public abstract JComponent getComponent();

    public abstract void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

    private class NodeCellEditor extends AbstractCellEditor implements TreeCellEditor {
        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            prepare(tree, value, isSelected, expanded, leaf, row, true);
            return getComponent();
        }

        @Override
        public Object getCellEditorValue() {
            return false;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }
    }
}
