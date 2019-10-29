package streamline.plugin.nodes.guts.components;

import streamline.plugin.nodes.guts.RefactoringNode;

import javax.swing.*;

public class EnabledRefactoringCheckBox extends JCheckBox {
    @org.jetbrains.annotations.NotNull
    private final RefactoringNode node;

    public EnabledRefactoringCheckBox(RefactoringNode node) {
        this.node = node;
        addActionListener((e) -> onAction());
        node.getListeners().add(() -> setSelected(node.getRefactoring().isEnabled()));
    }

    protected void onAction() {
        if (node.getRefactoring().setEnabled(isSelected())) {
            node.getListeners().fire();
        }
    }
}
