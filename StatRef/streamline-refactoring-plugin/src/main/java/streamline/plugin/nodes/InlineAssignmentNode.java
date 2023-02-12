package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.nodes.inlineUsage.InlineUsageNode;
import streamline.plugin.refactoring.InlineAssignment;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class InlineAssignmentNode extends CompoundNode {

    public InlineAssignmentNode(InlineAssignment inlineAssignment, NodesRegistry registry) {
        super(inlineAssignment, registry);
        setNodePanelParts(new RefactoringPresenter("Inline ", inlineAssignment.getInitializer().getInitializer().getElement()));
    }

    @Override
    public InlineVariableNode getParent() {
        return (InlineVariableNode) super.getParent();
    }
}
