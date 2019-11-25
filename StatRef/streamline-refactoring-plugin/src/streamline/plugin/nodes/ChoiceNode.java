package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.refactoring.RefactoringChoice;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChoiceNode extends RefactoringNode<RefactoringChoice> {
    private final List<SelfPresentingNode> nodes = new ArrayList<>();
    private final ButtonGroup buttons = new ButtonGroup();

    public ChoiceNode(NodesRegistry registry) {
        super(new RefactoringChoice(registry.getRefactorings()), registry);
        setNodePanelParts(new SimplePresenter());
    }

    public ChoiceNode add(RefactoringNode<?> node) {
        nodes.add(node);
        // TODO node presenter must go here
        node.setNodePanelParts(radioButton(node), textRenderer(new SimplePresenter()));
        refactoring.add(node.getRefactoring());
        if (refactoring.getVariants().size()==1) {
            refactoring.setChosen(node.getRefactoring());
        }
        return this;
    }

    @NotNull
    private Consumer<NodePanel> radioButton(RefactoringNode<?> node) {
        return panel -> {
            JRadioButton radioButton = new JRadioButton();
            buttons.add(radioButton);
            radioButton.addItemListener((e) -> {
                if (refactoring.setChosen(node.getRefactoring())) {
                    node.update();
                }
                SwingUtilities.invokeLater(() -> {
                    TreePath path = new TreePath(node.getNode().getPath());
                    if (radioButton.isSelected()) {
                        node.getTree().expandPath(path);
                    } else {
                        node.getTree().collapsePath(path);
                    }
                });
            });
            radioButton.setSelected(node.getRefactoring().equals(refactoring.getChosen()));
            panel.add(radioButton);
        };
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        return nodes;
    }

    @Override
    public boolean showRoot() {
        return false;
    }
}
