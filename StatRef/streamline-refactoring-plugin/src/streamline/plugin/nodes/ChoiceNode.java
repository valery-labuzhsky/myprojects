package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.refactoring.RefactoringChoice;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class ChoiceNode extends RefactoringNode<RefactoringChoice> {
    private final List<RefactoringNode> nodes = new ArrayList<>();
    private final ButtonGroup buttons = new ButtonGroup();

    public ChoiceNode(NodesRegistry registry) {
        super(new RefactoringChoice(registry.getRefactorings()), registry);
    }

    public ChoiceNode add(RefactoringNode<?> node) {
        nodes.add(node);
        node.setComponentFactory(() -> {
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
            return new NodePanel<>(radioButton);
        });
        refactoring.add(node.getRefactoring());
        if (refactoring.getVariants().size()==1) {
            refactoring.setChosen(node.getRefactoring());
        }
        return this;
    }

    @Override
    protected Presenter createPresenter() {
        return presentation -> {};
    }

    @NotNull
    @Override
    public SelfPresentingNode[] createChildren() {
        return nodes.toArray(new SelfPresentingNode[0]);
    }

    @Override
    public boolean showRoot() {
        return false;
    }
}
