package streamline.plugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class NodePanel extends NodeRendererComponent {
    private final JPanel panel = new JPanel();

    @Override
    public JPanel getComponent() {
        return NodePanel.this.panel;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.prepare(tree, value, selected, expanded, leaf, row, hasFocus);
        panel.invalidate();
    }

    protected void composePanel() {
        panel.add(createComponent());
        panel.add(super.getComponent());
    }

    @NotNull
    protected abstract JComponent createComponent();
}
