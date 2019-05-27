package streamline.plugin.refactoring.usage;

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
    private final VariantsController variants;
    private final IElement variant;

    private JRadioButton radioButton;

    public VariantElementNode(VariantsController variants, IElement variant) {
        super(variant.getProject(), "with ");
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
        if (variants.getNodes().size() == 1) {
            return super.createNodeComponent();
        }
        radioButton = new JRadioButton();
        updateRadioButton();
        radioButton.addActionListener(e ->
                variants.setSelected(variant));
        return new NodePanel(radioButton);
    }

    public void fireVariantChanged() {
        updateRadioButton();
        fireNodeChanged();
    }

    private void updateRadioButton() {
        radioButton.setSelected(variants.getSelected() == variant);
    }

}
