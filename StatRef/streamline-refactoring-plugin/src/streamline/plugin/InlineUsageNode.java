package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;

import java.util.ArrayList;

public class InlineUsageNode extends RefactoringNode<InlineUsage> {
    public InlineUsageNode(Project project, InlineUsage refactoring) {
        super(project, refactoring);
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        ArrayList<IElement> variants = refactoring.getVariants().getVariants();
        if (variants.size()==1) {
            return new SimpleNode[]{new VariantElementNode(myProject, variants.get(0))};
        } else {
            return new SimpleNode[]{new VariantsNode(myProject, refactoring.getVariants())};
        }
    }

    @Override
    protected PsiElement getPsiElement() {
        return refactoring.getUsage().getElement();
    }
}
