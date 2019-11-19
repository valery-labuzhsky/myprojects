package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class VariantElementNode extends SingleDescriptorNode {
    private final IInitializer variant;

    public VariantElementNode(InlineUsageNode parent, IInitializer variant) {
        super(variant.getElement().getProject());
        this.variant = variant;
        Listeners controller = parent.getListeners();
        controller.invoke(this::update);
        InlineUsage refactoring = parent.getRefactoring();
        setComponentFactory(() -> {
            JRadioButton radioButton = new JRadioButton();
            controller.invoke(() -> radioButton.setSelected(variant.equals(refactoring.getSelected())));
            radioButton.addActionListener(e -> {
                refactoring.setSelected(variant);
                refactoring.setEnabled(true);
                controller.fire();
            });
            return new PairNodePanel<>(radioButton);
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
    public List<? extends SelfPresentingNode> getChildren() {
        return Collections.emptyList();
    }
}
