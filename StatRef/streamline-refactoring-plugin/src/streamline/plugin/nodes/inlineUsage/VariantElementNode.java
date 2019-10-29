package streamline.plugin.nodes.inlineUsage;

import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.guts.ElementPresenter;
import streamline.plugin.nodes.guts.NodePanel;
import streamline.plugin.nodes.guts.NodeRendererComponent;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.guts.Listeners;

import javax.swing.*;

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
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{variant};
    }
}
