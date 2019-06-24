package streamline.plugin.refactoring.remove;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.RefactoringRegistry;

public class RemoveElementNode extends RefactoringNode<RemoveElement> {
    public RemoveElementNode(Project project, RemoveElement refactoring, NodesRegistry registry) {
        super(project, refactoring, registry);
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new RefactoringPresenter("Remove ", refactoring.getInitializer().getElement());
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        return new SimpleNode[0];
    }
}
