package streamline.plugin.refactoring.assignment;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.compound.CompoundRefactoring;
import streamline.plugin.refactoring.remove.RemoveElementNode;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AssignmentNode extends RefactoringNode<InlineAssignment> {

    public AssignmentNode(Project project, InlineAssignment inlineAssignment) {
        super(project, inlineAssignment);
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new RefactoringPresenter("Inline ", refactoring.getInitializer().getElement());
    }

    @Override
    @NotNull
    public SimpleNode[] createChildren() {
        ArrayList<RefactoringNode> nodes = new ArrayList<>();
        for (InlineUsage usage : this.refactoring.getUsages()) {
            nodes.add(new InlineUsageNode(myProject, usage));
        }
        RemoveElementNode removeNode = new RemoveElementNode(myProject, this.refactoring.getRemove());
        getListeners().addListener(() -> {
            if (!removeNode.isIntervened()) {
                if (refactoring.tryRemove()) {
                    removeNode.getListeners().fireRefactoringChanged();
                }
            }
        });
        nodes.add(removeNode);
        return nodes.toArray(new SimpleNode[0]);
    }

    @Override
    public void addMutationListener(Consumer<Refactoring> consumer) {
        for (SimpleNode child : getChildren()) {
            if (child instanceof InlineUsageNode) {
                InlineUsageNode usageNode = (InlineUsageNode) child;
                usageNode.addMutationListener((a) -> {
                    if (usageNode.getRefactoring().getSelected()!=refactoring.getInitializer()) {
                        Refactoring b = usageNode.getRefactoring();
                        CompoundRefactoring compound = new CompoundRefactoring();
                        b.setEnabled(false);
                        compound.add(refactoring);
                        compound.add(a);

                        consumer.accept(compound);
                    }
                });
            }
        }
    }

}
