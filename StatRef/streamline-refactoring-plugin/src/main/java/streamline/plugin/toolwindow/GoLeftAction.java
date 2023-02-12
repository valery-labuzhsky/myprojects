package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;

import static com.intellij.util.ui.tree.TreeUtil.scrollToVisible;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public class GoLeftAction extends RefactoringToolPanelAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Tree tree = getToolPanel(e).getTree();
        TreePath lead = tree.getLeadSelectionPath();
        int row = tree.getRowForPath(lead);
        if (lead == null || row < 0) {
            select(tree, tree.getPathForRow(0));
        } else {
            TreePath parent = lead.getParentPath();
            if (parent != null) {
                if (tree.isRootVisible() || null != parent.getParentPath()) {
                    select(tree, parent);
                } else if (row > 0) {
                    select(tree, tree.getPathForRow(row - 1));
                }
            }
        }
    }

    private static void select(@NotNull JTree tree, @Nullable TreePath path) {
        if (path == null) return;
        tree.setSelectionPath(path);
        scrollToVisible(tree, path, false);
    }

}
