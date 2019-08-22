package streamline.plugin.nodes;

import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.CreateMethod;

public class CreateMethodNode extends RefactoringNode<CreateMethod> {
    public CreateMethodNode(CreateMethod refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected Presenter createPresenter() {
        return presentation -> {
            presentation.clearText();
            presentation.addText("Create method "+getRefactoring().getMethod().getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        };
    }
}
