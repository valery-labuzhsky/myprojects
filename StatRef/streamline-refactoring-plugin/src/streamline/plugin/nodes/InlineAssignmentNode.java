package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.nodes.inlineUsage.InlineUsageNode;
import streamline.plugin.refactoring.InlineAssignment;
import streamline.plugin.refactoring.InlineUsage;

import java.util.ArrayList;
import java.util.List;

public class InlineAssignmentNode extends RefactoringNode<InlineAssignment> {

    public InlineAssignmentNode(InlineAssignment inlineAssignment, NodesRegistry registry) {
        super(inlineAssignment, registry);
        setNodePanelParts(new RefactoringPresenter("Inline ", refactoring.getInitializer().getInitializer().getElement()));
    }

    @Override
    @NotNull
    public List<SelfPresentingNode> createChildren() {
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        for (InlineUsage usage : this.refactoring.getUsages()) {
            nodes.add(new InlineUsageNode(usage, registry));
        }
        nodes.add(new RemoveElementNode(this.refactoring.getRemove(), registry));
        return nodes;
    }

    @Override
    public InlineVariableNode getParent() {
        return (InlineVariableNode) super.getParent();
    }
}
