package streamline.plugin;

import javax.swing.*;

public class NodePanel extends NodeComponent {
    private final JPanel panel = new JPanel();
    private final NodeRendererComponent renderer = new NodeRendererComponent();

    protected NodePanel(JComponent component) {
        component.setOpaque(false);
        panel.add(component);
        panel.add(renderer.getComponent());
    }

    @Override
    public JPanel getComponent() {
        return NodePanel.this.panel;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.prepare(tree, value, selected, expanded, leaf, row, hasFocus);
        panel.invalidate();
    }
}
