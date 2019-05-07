package streamline.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.SInitializer;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

public class VariantElementNode extends ElementNode {
    private final VariantsNode variants;
    private final IElement variant;
    private RadioNodePanel panel;

    public VariantElementNode(Project project, VariantsNode variants, IElement variant) {
        super(project);
        this.variants = variants;
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

    @NotNull
    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    @Override
    protected NodeComponent createNodeComponent() {
        panel = new RadioNodePanel();
        panel.getRadioButton().addActionListener(e ->
                variants.setSelected(variant));
        return panel;
    }

    public void fireVariantChanged() {
        panel.getRadioButton().setSelected(variants.getSelected() == variant);
        model.nodeChanged(node);
    }
}
