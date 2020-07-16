package streamline.plugin.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.tree.TreePath;

/**
 * Created on 16.07.2020.
 *
 * @author unicorn
 */
public class EnableAllChildrenAction extends RefactoringToolWindowAction {
    public EnableAllChildrenAction() {
        super("Select All", "Select all children", AllIcons.Actions.Selectall);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        RefactoringToolWindow toolWindow = getToolWindow(e);
        Tree tree = toolWindow.getTree();
        TreePath path = tree.getSelectionPath();
        RefactoringNode root = toolWindow.root;
        SelfPresentingNode node = root.findNode(path);
        if (node instanceof RefactoringNode) {
            Refactoring refactoring = ((RefactoringNode) node).getRefactoring();
            // TODO disable select all + show tooltip? if conditions are not met
            refactoring.enableAll();
        }
    }
}
