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
        setNodePanelParts(new RefactoringPresenter("Inline ", refactoring.getInitializer().getElement()));
    }

    @Override
    @NotNull
    public List<SelfPresentingNode> createChildren() {
        RemoveElementNode removeNode = new RemoveElementNode(this.refactoring.getRemove(), registry);
        Runnable enabledListener = () -> {
            boolean anyEnabled = refactoring.getRemove().isEnabled();
            for (InlineUsage usage : refactoring.getUsages()) {
                if (usage.isEnabled() || anyEnabled) {
                    anyEnabled = true;
                    break;
                }
            }
            setEnabled(anyEnabled);
        };
        removeNode.getListeners().invoke(enabledListener);
        Runnable usageListeners = new Runnable() {
            private boolean oldUsageLeft = false;
            private boolean usagesLeft = false;

            @Override
            public void run() {
                usagesLeft = refactoring.areUsagesLeft();
                if (oldUsageLeft != usagesLeft) {
                    removeNode.setEnabled(!usagesLeft);

                    if (!usagesLeft) {
                        setEnabled(false);
                    }

                    oldUsageLeft = usagesLeft;
                }
            }
        };
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        for (InlineUsage usage : this.refactoring.getUsages()) {
            InlineUsageNode usageNode = new InlineUsageNode(usage, registry);
            usageNode.setAssignment(refactoring.getInitializer());
            usageNode.getListeners().invoke(usageListeners);
            usageNode.getListeners().invoke(enabledListener);
            nodes.add(usageNode);
        }
        nodes.add(removeNode);
        return nodes;
    }

}
