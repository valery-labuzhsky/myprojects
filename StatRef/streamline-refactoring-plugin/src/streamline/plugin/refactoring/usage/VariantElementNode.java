package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.SInitializer;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.ElementNode;
import streamline.plugin.nodes.NodeComponent;
import streamline.plugin.nodes.NodePanel;

import javax.swing.*;

public class VariantElementNode extends ElementNode {
    private final VariantsNode variants;
    private final IElement variant;

    private JRadioButton radioButton;

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
        radioButton = new JRadioButton();
        radioButton.addActionListener(e ->
                variants.setSelected(variant));
        return new NodePanel(radioButton);
    }

    public void fireVariantChanged() {
        radioButton.setSelected(variants.getSelected() == variant);
        fireNodeChanged();
    }

}
