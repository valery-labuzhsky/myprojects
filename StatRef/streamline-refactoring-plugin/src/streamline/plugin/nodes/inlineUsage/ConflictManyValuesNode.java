package streamline.plugin.nodes.inlineUsage;

import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.Presenter;
import streamline.plugin.nodes.guts.SelfPresentingNode;

import java.util.Collections;
import java.util.List;

public class ConflictManyValuesNode extends SelfPresentingNode {

    public ConflictManyValuesNode(Project project) {
        super(project);
        update();
        setNodePanelParts(textRenderer(createPresenter()));
    }

    protected Presenter createPresenter() {
        return presentation -> {
            presentation.clearText();
            presentation.addText("Conflict: there are many possible values", SimpleTextAttributes.ERROR_ATTRIBUTES);
        };
    }

    @NotNull
    @Override
    public List<? extends SelfPresentingNode> getChildren() {
        return Collections.emptyList();
    }
}
