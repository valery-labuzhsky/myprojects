package streamline.plugin.nodes.guts;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TextDescriptor extends PresentableNodeDescriptor<TextDescriptor> {
    private final Presenter presenter;

    public TextDescriptor(Project project, Presenter presenter) {
        super(project, null);
        this.presenter = presenter;
        update();
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presenter.update(presentation);
    }

    @Override
    public TextDescriptor getElement() {
        return this;
    }
}
