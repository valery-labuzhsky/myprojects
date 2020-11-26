package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.KeyEventDispatcher;
import streamline.plugin.nodes.guts.NodeComponent;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SelfPresentingNode;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class RefactoringToolPanel extends SimpleToolWindowPanel {
    // TODO inline variable
    //  boolean c = true;
    //  test(c);

    // TODO inline generic

//    java.lang.RuntimeException: Conversion is not registered for com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl
//    at statref.model.idea.FunctionRegistry.convert(FunctionRegistry.java:16)
//    at statref.model.idea.IFactory.getElement(IFactory.java:103)
//    at statref.model.idea.IElement.getElement(IElement.java:72)
//    at statref.model.idea.IVariableDeclaration.getInitializer(IVariableDeclaration.java:26)
//    at streamline.plugin.nodes.InlineAssignmentNode.<init>(InlineAssignmentNode.java:18)
//    at statref.model.idea.BiFunctionRegistry.convert(BiFunctionRegistry.java:14)
//    at streamline.plugin.nodes.guts.NodesRegistry.create(NodesRegistry.java:37)

    final AnActionEvent originalEvent;
    RefactoringNode root;
    private final Tree tree = new Tree();

    public RefactoringToolPanel(AnActionEvent originalEvent, String displayName) {
        super(true, true);
        this.originalEvent = originalEvent;

        setupToolbar();
        setupTree();
        registerToolPanel(displayName);
    }

    public <N extends RefactoringNode> N setRoot(N root) {
        TreePath[] paths = getTree().getSelectionPaths();

        this.root = root;
        DefaultTreeModel model = new DefaultTreeModel(null);
        root.setTree(tree);
        model.setRoot(root);
        tree.setRootVisible(root.showRoot());
        tree.setModel(model);
        root.afterTreeNodeCreated();

        if (paths != null) {
            for (TreePath path : paths) {
                SelfPresentingNode n = root.findNode(path);
                if (n != null) {
                    n.select();
                }
            }
        }
        return root;
    }

    public Tree getTree() {
        return tree;
    }

    private void setupTree() {
        setContent(tree);
        tree.setCellRenderer(new ProxyNodeComponent());
        tree.setRowHeight(0);
        tree.setEditable(true);
        tree.setCellEditor(new ProxyCellEditor());
        tree.setSelectionRow(0);
        forwardMouseEvents();
        forwardKeyEvents();
    }

    private void forwardKeyEvents() {
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

    private void forwardMouseEvents() {
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
        DefaultActionGroup actionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction(RefactoringToolPanel.class.getName() + ".toolbar");

        for (AnAction action : actionGroup.getChildren(originalEvent)) {
            action.registerCustomShortcutSet(tree, null);
        }

        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
    }

    public Refactoring getRefactoring() {
        return root.getRefactoring();
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

    @NotNull
    ToolWindow getToolWindow(AnActionEvent event) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(event.getProject());
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Streamline");
        toolWindow.show(null);
        return toolWindow;
    }

    private void registerToolPanel(String displayName) {
        ContentManager contentManager = getToolWindow(originalEvent).getContentManager();

        Content tab = contentManager.getFactory().createContent(this, displayName, true);
        contentManager.addContent(tab);
        contentManager.setSelectedContent(tab);
        getTree().requestFocusInWindow();
    }

    void close(AnActionEvent event) {
        ContentManager contentManager = getToolWindow(event).getContentManager();
        contentManager.removeContent(contentManager.getContent(this), true);
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
            return component == null ? false : component.isEditable(e);
        }
    }

}
