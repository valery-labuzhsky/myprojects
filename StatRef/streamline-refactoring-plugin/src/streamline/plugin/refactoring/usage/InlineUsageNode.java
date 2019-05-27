package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.RefactoringNode;

import java.util.ArrayList;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {

    public InlineUsageNode(Project project, InlineUsage refactoring) {
        super(project, refactoring, "Replace ");
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        VariantsController variants = new VariantsController(refactoring);
        if (refactoring.getVariants().size() == 1) {
            return new SimpleNode[]{new VariantElementNode(variants, refactoring.getVariants().get(0))};
        }
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        ConflictManyValuesNode conflict = new ConflictManyValuesNode(myProject);
        nodes.add(conflict);
        nodes.addAll(variants.getNodes());
        return nodes.toArray(new SimpleNode[0]);
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getUsage().getElement();
    }
}
