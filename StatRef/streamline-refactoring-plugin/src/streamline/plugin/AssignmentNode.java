package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;

public class AssignmentNode extends RefactoringNode<InlineAssignment> {
    public AssignmentNode(Project project, InlineAssignment inlineAssignment) {
        super(project, inlineAssignment);
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getVariable().getElement();
    }

    @Override
    @NotNull
    public SimpleNode[] createChildren() {
        return refactoring.getUsages().stream().map(ch -> new InlineUsageNode(myProject, ch)).toArray(SimpleNode[]::new);
    }
}
