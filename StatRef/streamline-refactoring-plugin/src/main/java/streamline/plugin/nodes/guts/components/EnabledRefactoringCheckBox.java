package streamline.plugin.nodes.guts.components;

import streamline.plugin.nodes.guts.RefactoringNode;

import javax.swing.*;

public class EnabledRefactoringCheckBox extends JCheckBox {
    @org.jetbrains.annotations.NotNull
    private final RefactoringNode node;

    public EnabledRefactoringCheckBox(RefactoringNode node) {
        this.node = node;
        addActionListener((e) -> onAction());
        node.getListeners().invoke(() -> setSelected(node.getRefactoring().isEnabled()));
    }

    private void onAction() {
        node.setEnabled(isSelected());
    }
}
