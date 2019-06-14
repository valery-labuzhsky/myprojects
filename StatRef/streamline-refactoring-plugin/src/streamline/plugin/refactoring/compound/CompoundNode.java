package streamline.plugin.refactoring.compound;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.Presenter;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Listeners;
import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;

public class CompoundNode extends RefactoringNode<CompoundRefactoring> {
    public CompoundNode(Project project, CompoundRefactoring refactoring) {
        super(project, refactoring);
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        for (Refactoring r : refactoring.getRefactorings()) {
            nodes.add(RefactoringNode.create(getProject(), r));
        }
        return nodes.toArray(new SimpleNode[0]);
    }

    @Override
    protected Presenter createPresenter() {
        return presentation -> {};
    }

    @Override
    public boolean showRoot() {
        return false;
    }
}
