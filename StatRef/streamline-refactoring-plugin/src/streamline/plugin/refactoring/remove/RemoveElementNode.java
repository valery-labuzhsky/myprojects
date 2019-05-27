package streamline.plugin.refactoring.remove;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.RefactoringNode;

public class RemoveElementNode extends RefactoringNode<RemoveElement> {
    public RemoveElementNode(Project project, RemoveElement refactoring) {
        super(project, refactoring, "Remove ");
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getInitializer().getElement();
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        return new SimpleNode[0];
    }
}
