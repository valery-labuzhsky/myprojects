package streamline.plugin.nodes.inlineUsage;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.ElementPresenter;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class VariantElementNode extends SelfPresentingNode {
    private final InlineUsage variant;

    public VariantElementNode(InlineUsageNode parent, InlineUsage variant) {
        super(variant.getValue().getElement().getProject());
        this.variant = variant;
        Listeners controller = parent.getListeners();
        controller.invoke(this::update);
        setNodePanelParts(panel -> {
            JRadioButton radioButton = new JRadioButton();
            controller.invoke(() -> radioButton.setSelected(variant.isEnabled()));
            radioButton.addActionListener(e -> {
                variant.setEnabled(true);
                controller.fire();
                parent.getParent().getParent().select(variant);
            });
            panel.add(radioButton);
            panel.dispatchKeyEvents(radioButton);
        }, textRenderer(createPresenter()));
    }

    public VariantElementNode lock() {
        setNodePanelParts(textRenderer(createPresenter()));
        return this;
    }

    private ElementPresenter createPresenter() {
        return new ElementPresenter("", variant.getValue().getInitializer().getElement());
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> getChildren() {
        return Collections.emptyList();
    }
}
