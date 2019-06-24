package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Refactoring;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    private IInitializer assignment;

    public InlineUsageNode(Project project, InlineUsage refactoring, NodesRegistry registry) {
        super(project, refactoring, registry);
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

        getListeners().add(this::createWhatElseNodes);
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        if (refactoring.getVariants().size() == 1) {
            return new SimpleNode[]{new VariantElementNode(this, refactoring.getVariants().get(0)).lock()};
        }
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        if (assignment != null) {
            nodes.add(new VariantElementNode(this, assignment).lock());
        }
        ConflictManyValuesNode conflict = new ConflictManyValuesNode(myProject);
        nodes.add(conflict);
        for (IInitializer variant : refactoring.getVariants()) {
            if (!variant.equals(assignment)) {
                nodes.add(new VariantElementNode(this, variant));
            }
        }
        return nodes.toArray(new SimpleNode[0]);
    }

    private void createWhatElseNodes() {
        LinkedHashSet<Refactoring> newOnes = new LinkedHashSet<>(refactoring.whatElse());
        HashSet<SimpleNode> fixedNodes = new HashSet<>(Arrays.asList(getChildren()));
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
            getNode().add(RefactoringNode.create(getProject(), refactoring, registry).createTreeNode(getTree()));
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
