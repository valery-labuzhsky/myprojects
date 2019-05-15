package streamline.plugin.nodes;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.Refactoring;

public abstract class RefactoringNode<R extends Refactoring> extends ElementNode {
    protected final R refactoring;
    private SimpleNode[] children;

    public RefactoringNode(Project project, R refactoring) {
        super(project);
        this.refactoring = refactoring;
        update();
    }

    @Override
    public SimpleNode[] getChildren() {
        if (children == null) {
            children = createChildren();
        }
        return children;
    }

    @NotNull
    public abstract SimpleNode[] createChildren();
}
