package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.InlineAssignmentNode;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;

import java.util.ArrayList;
import java.util.List;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    public InlineUsageNode(InlineUsage refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new RefactoringPresenter("Replace ", this.refactoring.getUsage().getElement()));
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();

        if (!refactoring.getVariants().isEmpty()) {
            ConflictManyValuesNode conflict = new ConflictManyValuesNode(getProject());
            nodes.add(conflict);

            for (InlineUsage variant : refactoring.getVariants()) {
                nodes.add(new VariantElementNode(this, variant));
            }
        }
        return nodes;
    }

    @Override
    public InlineAssignmentNode getParent() {
        return (InlineAssignmentNode) super.getParent();
    }
}
