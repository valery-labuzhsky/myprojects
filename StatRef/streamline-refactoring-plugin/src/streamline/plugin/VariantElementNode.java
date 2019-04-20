package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import statref.model.SInitializer;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

public class VariantElementNode extends ElementNode {
    private final IElement variant;

    public VariantElementNode(Project project, IElement variant) {
        super(project);
        this.variant = variant;
        update();
    }

    @Override
    protected PsiElement getPsiElement() {
        if (variant instanceof SInitializer) {
            return ((IInitializer) variant).getInitializer().getElement();
        }
        return variant.getElement();
    }

    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }
}
