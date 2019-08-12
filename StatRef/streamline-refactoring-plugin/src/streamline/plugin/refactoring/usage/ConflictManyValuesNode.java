package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.Presenter;
import streamline.plugin.nodes.SelfPresentingNode;

public class ConflictManyValuesNode extends SelfPresentingNode {

    public ConflictManyValuesNode(Project project) {
        super(project);
        update();
    }

    @Override
    protected Presenter createPresenter() {
        return presentation -> {
            presentation.clearText();
            presentation.addText("Conflict: there are many possible values", SimpleTextAttributes.ERROR_ATTRIBUTES);
        };
    }

    @NotNull
    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }
}
