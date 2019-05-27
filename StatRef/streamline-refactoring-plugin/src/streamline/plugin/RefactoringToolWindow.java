package streamline.plugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.StudioIcons;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.NodeComponent;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.nodes.SelfPresentingNode;
import streamline.plugin.refactoring.Refactoring;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RefactoringToolWindow extends SimpleToolWindowPanel {
    private final RefactoringNode node;

    public RefactoringToolWindow(RefactoringNode node) {
        super(true, true);
        this.node = node;
        setupToolbar();
        setupTree();
    }

    private void setupTree() {
        DefaultTreeModel model = new DefaultTreeModel(null);
        Tree tree = new Tree(model);
        DefaultMutableTreeNode rootNode = node.createTreeNode(tree);
        model.setRoot(rootNode);
        tree.setCellRenderer(new ProxyNodeComponent());
        forwardMouseEvents(tree);
        expandTree(tree);
        setContent(tree);
    }

    private void expandTree(Tree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void forwardMouseEvents(Tree tree) {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                passEvent(event, tree);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                passEvent(event, tree);
            }

            public void passEvent(MouseEvent event, Tree tree11) {
                TreePath path = tree11.getPathForLocation(event.getX(), event.getY());
                if (path==null) return;
                Rectangle nodeBounds = tree11.getPathBounds(path);
                int row = tree11.getRowForPath(path);
                Component editingComponent = tree11.getCellRenderer().getTreeCellRendererComponent(
                        tree11, path.getLastPathComponent(), tree11.isPathSelected(path), tree11.isExpanded(path),
                        tree11.getModel().isLeaf(path.getLastPathComponent()), row, true);
                editingComponent.setBounds(0, 0,
                        nodeBounds.width,
                        nodeBounds.height);
                Point componentPoint = new Point(event.getX() - nodeBounds.x, event.getY() - nodeBounds.y);
                Component activeComponent = SwingUtilities.
                        getDeepestComponentAt(editingComponent,
                                componentPoint.x, componentPoint.y);
                MouseEvent newEvent = new MouseEvent(activeComponent,
                        event.getID(),
                        event.getWhen(),
                        event.getModifiers()
                                | event.getModifiersEx(),
                        componentPoint.x, componentPoint.y,
                        event.getXOnScreen(),
                        event.getYOnScreen(),
                        event.getClickCount(),
                        event.isPopupTrigger(),
                        event.getButton());
                activeComponent.dispatchEvent(newEvent);
            }
        });
    }

    private void setupToolbar() {
        AnAction refactor = getRefactorAction();
        // TODO it should be run default inline action
        AnAction refactor2 = new AnAction("Refactor", "Refactor", StudioIcons.Shell.Toolbar.INSTANT_RUN) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        };
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(refactor);
        actionGroup.addSeparator();
        actionGroup.add(refactor2);
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);
        actionToolbar.setTargetComponent(this);

        setToolbar(actionToolbar.getComponent());
    }

    @NotNull
    private AnAction getRefactorAction() {
        return ActionManager.getInstance().getAction(RefactorAction.class.getName());
    }

    public Refactoring getRefactoring() {
        return node.getRefactoring();
    }

    private static class ProxyNodeComponent implements TreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            return getComponent(value).createRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }

        public NodeComponent getComponent(Object value) {
            Object node = TreeUtil.getUserObject(value);
            if (node instanceof SelfPresentingNode) {
                return ((SelfPresentingNode) node).getNodeComponent();
            } else {
                throw new IllegalArgumentException(node + " is not supported");
            }
        }
    }

}
