package streamline.plugin.nodes.guts;

import javax.swing.*;

public class PairNodePanel<C extends JComponent> extends NodeComponent {
    private final JPanel panel = new JPanel();
    private final NodeRendererComponent renderer = new NodeRendererComponent();
    private final C nodeComponent;

    public PairNodePanel(C nodeComponent) {
        this.nodeComponent = nodeComponent;
        nodeComponent.setOpaque(false);
        panel.add(nodeComponent);
        panel.add(renderer.getComponent());
        panel.addKeyListener((KeyEventDispatcher) nodeComponent::dispatchEvent);
    }

    public C getNodeComponent() {
        return nodeComponent;
    }

    @Override
    public JPanel getComponent() {
        return PairNodePanel.this.panel;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.prepare(tree, value, selected, expanded, leaf, row, hasFocus);
        panel.invalidate();
    }
}
