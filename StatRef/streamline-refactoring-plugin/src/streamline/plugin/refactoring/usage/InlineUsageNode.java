package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.RefactoringNode;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    public InlineUsageNode(Project project, InlineUsage refactoring) {
        super(project, refactoring, "Replace ");
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        return new SimpleNode[]{new VariantsNode(myProject, refactoring)};
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getUsage().getElement();
    }
}
