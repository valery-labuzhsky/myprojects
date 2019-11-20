package streamline.plugin.nodes.guts;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class TextRenderer extends NodeComponent {
    private final TextDescriptor text;
    private final NodeRenderer renderer = new NodeRenderer();

    public TextRenderer(Project project, Presenter presenter) {
        text = new TextDescriptor(project, presenter);
    }

    @Override
    public JComponent getComponent() {
        return renderer;
    }

    public boolean update() {
        return text.update();
    }

    @Override
    public void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, this.text, selected, expanded, leaf, row, hasFocus);
    }
}
