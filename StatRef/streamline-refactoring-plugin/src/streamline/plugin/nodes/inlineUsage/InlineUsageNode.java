package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.tree.DefaultMutableTreeNode;
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
        for (DefaultMutableTreeNode ascendant = (DefaultMutableTreeNode) getNode().getParent();
             ascendant != null;
             ascendant = (DefaultMutableTreeNode) ascendant.getParent()) {
            Object userObject = ascendant.getUserObject();
            if (userObject instanceof RefactoringNode) {
                if (refactoring.equals(((RefactoringNode) userObject).getRefactoring())) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getNode().getParent();
                    int index = parent.getIndex(getNode());
                    parent.remove(index);
                    getModel().nodesWereRemoved(parent, new int[]{index}, new Object[]{this});
                    return;
                }
            }
        }

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
        if (assignment != null) {
            nodes.add(new VariantElementNode(this, assignment).lock());
        }
        ConflictManyValuesNode conflict = new ConflictManyValuesNode(getProject());
        nodes.add(conflict);
        for (IInitializer variant : refactoring.getVariants()) {
            if (!variant.equals(assignment)) {
                nodes.add(new VariantElementNode(this, variant));
            }
        }
        return nodes;
    }

    private void createWhatElseNodes() {
        LinkedHashSet<Refactoring> newOnes = new LinkedHashSet<>(refactoring.whatElse());
        HashSet<SelfPresentingNode> fixedNodes = new HashSet<>(getChildren());
        ArrayList<Integer> removedIndices = new ArrayList<>();
        ArrayList<Object> removedObjects = new ArrayList<>();
        for (int i = 0; i < getNode().getChildCount(); i++) {
            TreeNode node = getNode().getChildAt(i);
            if (node instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
                if (!fixedNodes.remove(userObject)) {
                    if (userObject instanceof RefactoringNode) {
                        RefactoringNode refactoringNode = (RefactoringNode) userObject;
                        if (!newOnes.remove(refactoringNode.getRefactoring())) {
                            removedIndices.add(i);
                            removedObjects.add(refactoringNode);
                        }
                    }
                }
            }
        }

        for (int i = removedIndices.size() - 1; i >= 0; i--) {
            Integer index = removedIndices.get(i);
            getNode().remove(index);
        }

        getModel().nodesWereRemoved(getNode(), getInts(removedIndices), removedObjects.toArray());

        int[] insertedIndices = new int[newOnes.size()];
        int i = 0;
        for (Refactoring refactoring : newOnes) {
            insertedIndices[i++] = getNode().getChildCount();
            getNode().add(registry.create(refactoring).createTreeNode(getTree()));
        }

        getModel().nodesWereInserted(getNode(), insertedIndices);

        for (int index : insertedIndices) {
            ((RefactoringNode) ((DefaultMutableTreeNode) getNode().getChildAt(index)).getUserObject()).afterTreeNodeCreated();
        }
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
