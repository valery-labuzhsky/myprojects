package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;

public abstract class SelfPresentingNode extends SimpleNode {
    private NodeComponent component;

    public SelfPresentingNode(Project project) {
        super(project);
    }

    protected NodeComponent createNodeComponent() {
        return new NodeRendererComponent();
    }

    public NodeComponent getNodeComponent() {
        if (component==null) {
            component = createNodeComponent();
        }
        return component;
    }
}
