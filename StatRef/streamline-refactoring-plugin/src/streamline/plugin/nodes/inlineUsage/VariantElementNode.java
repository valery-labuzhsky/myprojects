package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.guts.ElementPresenter;
import streamline.plugin.nodes.guts.NodePanel;
import streamline.plugin.nodes.guts.NodeRendererComponent;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.*;
import java.util.Objects;

public class VariantElementNode extends SelfPresentingNode {
    private final IInitializer variant;

    public VariantElementNode(InlineUsageNode parent, IInitializer variant) {
        super(variant.getElement().getProject());
        this.variant = variant;
        Listeners controller = parent.getListeners();
        controller.add(this::update);
        InlineUsage refactoring = parent.getRefactoring();
        setComponentFactory(() -> {
            JRadioButton radioButton = new JRadioButton();
            controller.add(() -> radioButton.setSelected(variant.equals(refactoring.getSelected())));
            radioButton.addActionListener(e -> {
                refactoring.setSelected(variant);
                refactoring.setEnabled(true);
                controller.fire();
            });
            return new NodePanel<>(radioButton);
        });
    }

    public VariantElementNode lock() {
        setComponentFactory(NodeRendererComponent::new);
        return this;
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new ElementPresenter("with ", variant.getInitializer().getElement());
    }

    @NotNull
    @Override
    public SelfPresentingNode[] getChildren() {
        return new SelfPresentingNode[0];
    }

    // TODO why?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantElementNode that = (VariantElementNode) o;
        return variant.equals(that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variant);
    }
}
