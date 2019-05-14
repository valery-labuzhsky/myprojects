package streamline.plugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.StudioIcons;
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SInitializer;
import statref.model.idea.IElement;
import statref.model.idea.IVariable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SLInlineAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO I need to: inline last value set if it is determined.
        // TODO If it's not - conflict: variants: manual editing, use one of the values
        // TODO on inline I must escape level of expression, and unescape when appropriate

        // TODO should I write tests from the beginning? probably yes, but I need do it manually first to keep me interested

        // TODO when should I add refactoring tree view? probably after some refactorings are already implemented, I should just not forget to leave comments
        // TODO the tests are hard to run, and they don't work anyway
        // TODO I may also try idea's tests

        PsiReferenceExpression reference = getPsiElement(event, PsiReferenceExpression.class);
        // TODO it doesn't work with declaration
        // TODO I should run default action if element is not supported
        if (reference != null) {
            IVariable variable = new IVariable(reference);

            Project project = getEventProject(event);
            // TODO not necessary doing it in write command as refactoring will occur on button pressed
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // TODO do we need to add it to PSI? why not? but not right now
                if (variable.isAssignment()) {
                    InlineAssignment refactoring = new InlineAssignment(variable);
                    // TODO now I must improve a tree to make in comfortable to work with
                    // TODO I need to make things doable with controls emulating hotkeys just to show tooltips

                    // TODO I should not display a tree if there are no conflicts
                    createRefactoringTree(project, "Inline " + variable.getText(), refactoring);
                } else {
                    ArrayList<IElement> variants = new AssignmentFlow(variable).getVariants(variable);
                    SInitializer initializer = null;
                    if (variants.size() == 0) {
                        // TODO show error
                        // TODO why error? there are no usages - remove it
                    } else if (variants.size() == 1) {
                        initializer = (SInitializer) variants.get(0);
                    } else {
                        InlineVariableDialog dialog = new InlineVariableDialog(project, variants);
                        if (dialog.showAndGet()) {
                            initializer = (SInitializer) dialog.getSelectedValue();
                        }
                    }
                    // TODO next thing is to check whether there are usages left
                    // TODO inline variable set instead of usage

                    if (initializer != null) {
                        variable.replace(initializer.getInitializer()); // TODO uncomment me
                        // TODO check if any usages left
                    }
                }
            });
        } else {
            // TODO show error message here
        }
    }

    private void createRefactoringTree(Project project, String displayName, InlineAssignment refactoring) {
        DefaultTreeModel model = new DefaultTreeModel(null);
        Tree tree = new Tree(model);
        AssignmentNode node = new AssignmentNode(project, refactoring);
        DefaultMutableTreeNode rootNode = node.createTreeNode(tree);
        model.setRoot(rootNode);

        ContentManager contentManager = getStreamlineToolWindow(project);

        tree.setCellRenderer(new ProxyNodeComponent());
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                passEvent(event, tree);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                passEvent(event, tree);
            }

            public void passEvent(MouseEvent event, Tree tree) {
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                if (path==null) return;
                Rectangle nodeBounds = tree.getPathBounds(path);
                int row = tree.getRowForPath(path);
                Component editingComponent = tree.getCellRenderer().getTreeCellRendererComponent(
                        tree, path.getLastPathComponent(), tree.isPathSelected(path), tree.isExpanded(path),
                        tree.getModel().isLeaf(path.getLastPathComponent()), row, true);
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

        SimpleToolWindowPanel toolWindow = new SimpleToolWindowPanel(true, true);
        AnAction refactor = new AnAction("Refactor", "Refactor", StudioIcons.Shell.Toolbar.RUN) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                WriteCommandAction.runWriteCommandAction(e.getProject(), refactoring::refactor);
            }
        };
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
        actionToolbar.setTargetComponent(toolWindow);

        toolWindow.setToolbar(actionToolbar.getComponent());
        toolWindow.setContent(tree);

        Content tab = contentManager.getFactory().createContent(toolWindow, displayName, true);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        contentManager.addContent(tab);
        contentManager.setSelectedContent(tab);
    }

    private class ProxyNodeComponent implements TreeCellRenderer {
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

    private ContentManager getStreamlineToolWindow(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Streamline");
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow("Streamline", true, ToolWindowAnchor.RIGHT);
        }
        toolWindow.show(null);
        return toolWindow.getContentManager();
    }

    private <P extends PsiElement> P getPsiElement(AnActionEvent event, Class<P> aClass) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        if (editor != null && file != null) {
            int offset = editor.getCaretModel().getOffset();
            P element = PsiTreeUtil.getParentOfType(file.findElementAt(offset), aClass);
            if (element == null) {
                return PsiTreeUtil.getParentOfType(file.findElementAt(offset - 1), aClass);
            }
            return element;
        }
        return null;
    }

    private static class InlineVariableDialog extends DialogWrapper {
        private final ArrayList<IElement> variants;

        private JBList<IElement> list;

        public InlineVariableDialog(Project project, ArrayList<IElement> variants) {
            super(project);
            this.variants = variants;
            init();
            setTitle("Inline");
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return list;
        }

        @NotNull
        @Override
        protected JComponent createCenterPanel() {
            list = new JBList<>(Lists.reverse(variants));
            list.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, ((SInitializer) value).getInitializer().getText(), index, isSelected, cellHasFocus);
                }
            });
            list.setSelectedIndex(0);
            return list;
        }

        public IElement getSelectedValue() {
            return list.getSelectedValue();
        }
    }

}
