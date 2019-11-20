package streamline.plugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.KeyEventDispatcher;
import streamline.plugin.nodes.guts.NodeComponent;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class RefactoringToolWindow extends SimpleToolWindowPanel {
    private final AnActionEvent event;
    private RefactoringNode node;
    private final Tree tree = new Tree();

    public RefactoringToolWindow(AnActionEvent event) {
        super(true, true);
        this.event = event;
        setupToolbar();
        setupTree();
    }

    public void setNode(RefactoringNode node) {
        TreePath[] paths = getTree().getSelectionPaths();

        this.node = node;
        DefaultTreeModel model = new DefaultTreeModel(null);
        DefaultMutableTreeNode rootNode = node.createTreeNode(tree);
        model.setRoot(rootNode);
        tree.setRootVisible(node.showRoot());
        tree.setModel(model);
        node.afterTreeNodeCreated();

        if (paths != null) {
            for (TreePath path : paths) {
                SelfPresentingNode n = node.findNode(path);
                if (n != null) {
                    n.select();
                }
            }
        }
    }

    public Tree getTree() {
        return tree;
    }

    private void setupTree() {
        setContent(tree);
        tree.setCellRenderer(new ProxyNodeComponent());
        tree.setEditable(true);
        tree.setCellEditor(new ProxyCellEditor());
        tree.setSelectionRow(0);
        forwardMouseEvents(tree);
        forwardKeyEvents(tree);
    }

    private void forwardKeyEvents(Tree tree) {
        tree.addKeyListener((KeyEventDispatcher) e -> {
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                for (TreePath path : paths) {
                    Object value = path.getLastPathComponent();
                    tree.getCellRenderer().getTreeCellRendererComponent(
                            tree, value, true, true,
                            tree.getModel().isLeaf(value), tree.getRowForPath(path), true).
                            dispatchEvent(e);
                }
            }
        });
    }

    private void forwardMouseEvents(Tree tree) {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                passEvent(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                passEvent(event);
            }

            public void passEvent(MouseEvent event) {
                if (tree.isEditing()) {
                    return;
                }
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                if (path == null) return;
                Rectangle nodeBounds = tree.getPathBounds(path);
                Object value = path.getLastPathComponent();
                Component editingComponent = tree.getCellRenderer().getTreeCellRendererComponent(
                        tree, value, tree.isPathSelected(path), tree.isExpanded(path),
                        tree.getModel().isLeaf(value), tree.getRowForPath(path), true);
                editingComponent.setBounds(nodeBounds);
                Point componentPoint = new Point(event.getX() - nodeBounds.x, event.getY() - nodeBounds.y);
                Component activeComponent = SwingUtilities.
                        getDeepestComponentAt(editingComponent,
                                componentPoint.x, componentPoint.y);
                translate(componentPoint, editingComponent, activeComponent);
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

            private void translate(Point point, Component from, Component to) {
                while (to != from) {
                    point.translate(-to.getX(), -to.getY());
                    to = to.getParent();
                }
            }
        });
    }

    private void setupToolbar() {
        AnAction refactor = getRefactorAction();
        AnAction defaultInline = new AnAction("Default", "IDEA native inline action", AllIcons.Actions.Run_anything) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AnAction nativeAction = ActionManager.getInstance().getAction("Inline");
                ActionUtil.performActionDumbAware(nativeAction, event);
            }
        };
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(refactor);
        actionGroup.addSeparator();
        actionGroup.add(defaultInline);
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);
        actionToolbar.setTargetComponent(this);
        actionGroup.registerCustomShortcutSet(tree, null);
        setToolbar(actionToolbar.getComponent());
    }

    @NotNull
    private AnAction getRefactorAction() {
        RefactorAction action = new RefactorAction();
        action.copyShortcutFrom(ActionManager.getInstance().getAction(RefactorAction.class.getName()));
        return action;
    }

    public Refactoring getRefactoring() {
        return node.getRefactoring();
    }

    private NodeComponent getComponent(Object value) {
        Object node = TreeUtil.getUserObject(value);
        if (node instanceof SelfPresentingNode) {
            return ((SelfPresentingNode) node).getNodeComponent();
        } else if (node == null) {
            return null;
        } else {
            throw new IllegalArgumentException(node + " is not supported");
        }
    }

    private class ProxyNodeComponent implements TreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            NodeComponent component = getComponent(value);
            if (component == null) return new JLabel("Streamline");
            return component.createRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
    }

    private class ProxyCellEditor extends AbstractCellEditor implements TreeCellEditor {
        private Object value;

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            getTree().getExpandableItemsHandler().setEnabled(false);
            this.value = value;
            NodeComponent component = getComponent(value);
            if (component == null) return new JLabel("Streamline");
            return component.createRenderer().getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                tree.getExpandableItemsHandler().setEnabled(true);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            tree.getExpandableItemsHandler().setEnabled(true);
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            Object node;
            if (e instanceof MouseEvent) {
                Point point = ((MouseEvent) e).getPoint();
                node = tree.getClosestPathForLocation(point.x, point.y).getLastPathComponent();
            } else {
                node = tree.getLastSelectedPathComponent();
            }
            NodeComponent component = getComponent(node);
            return component.isEditable(e);
        }
    }
}
