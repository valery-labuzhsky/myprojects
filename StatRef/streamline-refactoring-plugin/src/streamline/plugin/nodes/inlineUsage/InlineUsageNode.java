package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    private IInitializer assignment;

    public InlineUsageNode(InlineUsage refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new RefactoringPresenter("Replace ", this.refactoring.getUsage().getElement()));
    }

    public void selectAny() {
        for (SelfPresentingNode child : getChildren()) {
            if (child instanceof VariantElementNode) {
                child.select();
                break;
            }
        }
    }

    @Override
    public void afterTreeNodeCreated() {
        super.afterTreeNodeCreated();

        getListeners().invoke(this::createWhatElseNodes);
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        if (refactoring.getVariants().size() == 1) {
            return Collections.singletonList(
                    new VariantElementNode(this, refactoring.getVariants().get(0)).lock());
        }
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        boolean lockedNode = false;
        // TODO I cannot unset it, I need to lock it only when assignment is actually selected
        // TODO the problem with lock is that it should actually reflect
        // TODO the other problem is that it doesn't recreate the children
        // TODO how should this tree look like?
        if (assignment != null && assignment.equals(refactoring.getSelected())) {
            nodes.add(new VariantElementNode(this, assignment).lock());
            lockedNode = true;
        }
        ConflictManyValuesNode conflict = new ConflictManyValuesNode(getProject());
        nodes.add(conflict);
        for (IInitializer variant : refactoring.getVariants()) {
            if (!lockedNode || !variant.equals(assignment)) {
                nodes.add(new VariantElementNode(this, variant));
            }
        }
        return nodes;
    }

    private void createWhatElseNodes() {
        LinkedHashSet<Refactoring> newOnes = new LinkedHashSet<>(refactoring.whatElse());

        notifyRemoved(filterChildren(node -> {
            if (node instanceof RefactoringNode) {
                if (!newOnes.remove(((RefactoringNode) node).getRefactoring())) {
                    return false;
                }
            }
            return true;
        }));

        int startIndex = this.getChildCount();
        for (Refactoring refactoring : newOnes) {
            addChild(refactoring);
        }
        int stopIndex = this.getChildCount();

        if (stopIndex > startIndex) {
            int[] indices = new int[stopIndex - startIndex];
            Arrays.setAll(indices, j -> startIndex + j);
            getModel().nodesWereInserted(this, indices);

            for (int index : indices) {
                ((RefactoringNode) getChildAt(index)).afterTreeNodeCreated();
            }
        }
    }

    private void notifyRemoved(LinkedHashMap<Integer, SelfPresentingNode> removed) {
        if (!removed.isEmpty()) {
            getModel().nodesWereRemoved(this, getInts(removed.keySet()), removed.values().toArray());
        }
    }

    private void addChild(Refactoring refactoring) {
        HashSet<TreeNode> visible = getVisibleNodes();
        RefactoringNode treeNode = registry.create(refactoring);
        if (visible.contains(treeNode)) {
            return;
        }
        removeRecursion(treeNode, visible);
        treeNode.setTree(getTree());
        getChildren().add(treeNode);
        treeNode.setParent(this);
    }

    @NotNull
    private HashSet<TreeNode> getVisibleNodes() {
        HashSet<TreeNode> visible = new HashSet<>();
        TreeNode ascendant = this;
        while (ascendant != null) {
            visible.add(ascendant);
            // Add siblings
            for (int i = 0; i < ascendant.getChildCount(); i++) {
                visible.add(ascendant.getChildAt(i));
            }
            ascendant = ascendant.getParent();
        }
        return visible;
    }

    private void removeRecursion(SelfPresentingNode treeNode, HashSet<TreeNode> visible) {
        treeNode.filterChildren(child -> {
            if (visible.contains(child)) {
                return false;
            }
            removeRecursion(child, visible);
            return true;
        });
    }

    private DefaultTreeModel getModel() {
        return (DefaultTreeModel) getTree().getModel();
    }

    private int[] getInts(Collection<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;
        for (Integer integer : integers) {
            ints[i++] = integer;
        }
        return ints;
    }

    public void setAssignment(IInitializer assignment) {
        this.assignment = assignment;
    }
}
