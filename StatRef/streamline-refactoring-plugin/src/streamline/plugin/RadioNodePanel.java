package streamline.plugin;

import com.intellij.ide.util.treeView.NodeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.EventObject;

public class RadioNodePanel extends NodeComponent {
    private final NodeRenderer renderer = new NodeRenderer();
    private final JPanel panel = new JPanel();

    public RadioNodePanel() {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        panel.add(radioButton);
        panel.add(renderer);
    }

    @Override
    public JPanel getComponent() {
        return RadioNodePanel.this.panel;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        panel.invalidate();
    }

}
