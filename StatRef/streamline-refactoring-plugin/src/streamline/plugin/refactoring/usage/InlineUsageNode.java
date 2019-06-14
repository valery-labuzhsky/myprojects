package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.assignment.InlineAssignment;

import java.util.ArrayList;
import java.util.function.Consumer;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    public InlineUsageNode(Project project, InlineUsage refactoring) {
        super(project, refactoring);
    }

    @Override
    public void addMutationListener(Consumer<Refactoring> consumer) {
        getListeners().addListener(() -> {
            if (refactoring.getSelected() != null) {
                InlineAssignment assignment = new InlineAssignment(refactoring.getSelected());
                assignment.setOnlyEnabled(refactoring);
                consumer.accept(assignment);
            }
        });
    }

    public void selectAny() {
        for (SimpleNode child : getChildren()) {
            if (child instanceof VariantElementNode) {
                ((VariantElementNode) child).select();
                break;
            }
        }
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new RefactoringPresenter("Replace ", refactoring.getUsage().getElement());
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        if (refactoring.getVariants().size() == 1) {
            return new SimpleNode[]{new VariantElementNode(this, refactoring.getVariants().get(0))};
        }
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        ConflictManyValuesNode conflict = new ConflictManyValuesNode(myProject);
        nodes.add(conflict);
        for (IInitializer variant : refactoring.getVariants()) {
            nodes.add(new VariantElementNode(this, variant));
        }
        return nodes.toArray(new SimpleNode[0]);
    }

}
