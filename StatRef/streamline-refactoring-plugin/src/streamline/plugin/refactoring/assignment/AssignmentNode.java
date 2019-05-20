package streamline.plugin.refactoring.assignment;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.remove.RemoveElementNode;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import java.util.ArrayList;

public class AssignmentNode extends RefactoringNode<InlineAssignment> {
    public AssignmentNode(Project project, InlineAssignment inlineAssignment) {
        super(project, inlineAssignment, "Inline ");
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getVariable().getElement();
    }

    @Override
    @NotNull
    public SimpleNode[] createChildren() {
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        for (InlineUsage usage : refactoring.getUsages()) {
            nodes.add(new InlineUsageNode(myProject, usage));
        }
        nodes.add(new RemoveElementNode(myProject, refactoring.getRemove()));
        return nodes.toArray(new SimpleNode[0]);
    }

}
