package streamline.plugin.refactoring.assignment;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.remove.RemoveElementNode;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import java.util.ArrayList;

public class AssignmentNode extends RefactoringNode<InlineAssignment> {

    public AssignmentNode(Project project, InlineAssignment inlineAssignment, NodesRegistry registry) {
        super(project, inlineAssignment, registry);
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new RefactoringPresenter("Inline ", refactoring.getInitializer().getElement());
    }

    @Override
    @NotNull
    public SimpleNode[] createChildren() {
        RemoveElementNode removeNode = new RemoveElementNode(myProject, this.refactoring.getRemove(), registry);
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
        removeNode.getListeners().add(enabledListener);
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
        ArrayList<RefactoringNode> nodes = new ArrayList<>();
        for (InlineUsage usage : this.refactoring.getUsages()) {
            InlineUsageNode usageNode = new InlineUsageNode(myProject, usage, registry);
            usageNode.setAssignment(refactoring.getInitializer());
            usageNode.getListeners().add(usageListeners);
            usageNode.getListeners().add(enabledListener);
            nodes.add(usageNode);
        }
        nodes.add(removeNode);
        return nodes.toArray(new SimpleNode[0]);
    }

}
