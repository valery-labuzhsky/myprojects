package streamline.plugin.nodes.guts;

import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class NodePanel extends NodeComponent {

    private final JPanel panel = new JPanel(new HorizontalLayout(5));

    private final List<NodeComponent> nodeComponents = new ArrayList<>();
    private final List<JComponent> editors = new ArrayList<>();

    public void resize() {
        panel.setSize(panel.getPreferredSize());
    }

    public void addEditor(JTextField editor) {
        add(editor);
        editors.add(editor);
    }

    public void addNodeComponent(NodeComponent component) {
        add(component.getComponent());
        nodeComponents.add(component);
    }

    public void add(JComponent component) {
        component.setOpaque(false);
        panel.add(component);
    }

    public void dispatchKeyEvents(JComponent component) {
        panel.addKeyListener((KeyEventDispatcher) component::dispatchEvent);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public boolean isEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            if (editors.isEmpty()) {
                return false;
            }
            MouseEvent me = (MouseEvent) e;
            Tree tree = (Tree) me.getSource();
            TreePath path = tree.getClosestPathForLocation(me.getX(), me.getY());
            Rectangle bounds = tree.getPathBounds(path);
            Point clickPoint = new Point(me.getX() - bounds.x, me.getY() - bounds.y);
            for (JComponent editor : editors) {
                if (editor.getBounds().contains(clickPoint)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        for (NodeComponent component : nodeComponents) {
            component.prepare(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        panel.invalidate();
    }

}
