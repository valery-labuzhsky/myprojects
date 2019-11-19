package streamline.plugin.nodes.guts;

import com.intellij.openapi.project.Project;

public abstract class SingleDescriptorNode extends SelfPresentingNode {
    private final Project project;
    private TextDescriptor nodeDescriptor;

    public SingleDescriptorNode(Project project) {
        this.project = project;
        onUpdate.listen(() -> getNodeDescriptor().update());
    }

    protected abstract Presenter createPresenter();

    protected Project getProject() {
        return project;
    }

    // TODO now move it to CreateMethodNode and refactor it there, then push it back
    public TextDescriptor getNodeDescriptor() {
        if (nodeDescriptor == null) {
            nodeDescriptor = new TextDescriptor(getProject(), createPresenter());
        }
        return nodeDescriptor;
    }

}
