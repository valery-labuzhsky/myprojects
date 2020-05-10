package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
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
        refactoring.onUpdate.listen(() -> getListeners().fire()); // TODO get rid of node listeners eventually?
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        // TODO make me part of InlineUsageNode
        nodes.add(new VariantElementNode(this, refactoring).lock());

        if (!refactoring.getVariants().isEmpty()) {
            ConflictManyValuesNode conflict = new ConflictManyValuesNode(getProject());
            nodes.add(conflict);

            for (InlineUsage variant : refactoring.getVariants()) {
                nodes.add(new VariantElementNode(this, variant));
            }
        }
        return nodes;
    }

    public void setAssignment(IInitializer assignment) {
    }
}
