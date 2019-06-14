package streamline.plugin.refactoring.usage;

import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.NodePanel;
import streamline.plugin.nodes.SelfPresentingNode;
import streamline.plugin.refactoring.Listeners;

import javax.swing.*;

public class VariantElementNode extends SelfPresentingNode {
    private final IInitializer variant;

    public VariantElementNode(InlineUsageNode parent, IInitializer variant) {
        super(variant.getElement().getProject());
        this.variant = variant;
        Listeners controller = parent.getListeners();
        controller.addListener(this::update);
        InlineUsage refactoring = parent.getRefactoring();
        if (refactoring.getVariants().size() > 1) {
            setComponentFactory(() -> {
                JRadioButton radioButton = new JRadioButton();
                // TODO I may change the tree all together
                // TODO how will I control it?
                // TODO generate a tree every time something changed?
                // TODO that will be a solution, why not
                // TODO Or I may want to reuse parts of it, but will go from parent to children anyway
                // TODO Do I need to waste my time to make anything
                // TODO let's be simple first and decide it later
                controller.addListener(() -> radioButton.setSelected(variant.equals(refactoring.getSelected())));
                radioButton.addActionListener(e -> {
                    refactoring.setSelected(variant);
                    controller.fireRefactoringChanged();
                });
                return new NodePanel<>(radioButton);
            });
        }
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
