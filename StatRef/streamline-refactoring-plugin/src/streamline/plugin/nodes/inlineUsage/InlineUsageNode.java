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

        int startIndex = getNode().getChildCount();
        for (Refactoring refactoring : newOnes) {
            addChild(refactoring);
        }
        int stopIndex = getNode().getChildCount();

        if (stopIndex > startIndex) {
            int[] indices = new int[stopIndex - startIndex];
            Arrays.setAll(indices, i -> startIndex + i);
            getModel().nodesWereInserted(getNode(), indices);

            for (int index : indices) {
                ((RefactoringNode) ((DefaultMutableTreeNode) getNode().getChildAt(index)).getUserObject()).afterTreeNodeCreated();
            }
        }
    }

    private void addChild(Refactoring refactoring) {
        HashSet<Refactoring> ascendants = new HashSet<>();
        DefaultMutableTreeNode ascendant = getNode();
        while (ascendant != null) {
            Object node = ascendant.getUserObject();
            if (node instanceof RefactoringNode) {
                ascendants.add(((RefactoringNode) node).getRefactoring());
                // Add siblings
                for (int i = 0; i < ascendant.getChildCount(); i++) {
                    TreeNode child = ascendant.getChildAt(i);
                    if (child instanceof DefaultMutableTreeNode) {
                        Object childNode = ((DefaultMutableTreeNode) child).getUserObject();
                        if (childNode instanceof RefactoringNode) {
                            ascendants.add(((RefactoringNode) childNode).getRefactoring());
                        }
                    }
                }
            }
            ascendant = (DefaultMutableTreeNode) ascendant.getParent();
        }
        if (ascendants.contains(refactoring)) {
            return;
        }
        DefaultMutableTreeNode treeNode = registry.create(refactoring).createTreeNode(getTree());
        removeRecursion(treeNode, ascendants);
        getNode().add(treeNode);
    }

    private void removeRecursion(TreeNode treeNode, HashSet<Refactoring> ascendants) {
        ArrayList<DefaultMutableTreeNode> remove = new ArrayList<>();
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            TreeNode child = treeNode.getChildAt(i);
            if (child instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) child).getUserObject();
                if (userObject instanceof RefactoringNode) {
                    if (ascendants.contains(((RefactoringNode) userObject).getRefactoring())) {
                        remove.add((DefaultMutableTreeNode) child);
                        continue;
                    }
                }
            }
            removeRecursion(child, ascendants);
        }
        for (DefaultMutableTreeNode node : remove) {
            node.removeFromParent();
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
