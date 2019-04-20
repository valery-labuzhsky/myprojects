package streamline.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SInitializer;
import statref.model.idea.IElement;
import statref.model.idea.IVariable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
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

        PsiReferenceExpression reference = getPsiElement(event, PsiReferenceExpression.class);
        // TODO it doesn work with declaration
        if (reference != null) {
            IVariable variable = new IVariable(reference);

            Project project = getEventProject(event);
            // TODO create a test?
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // TODO do we need to add it to PSI? why not? but not right now
                if (variable.isAssignment()) {
                    InlineAssignment inlineAssignment = new InlineAssignment(variable);
                    for (IVariable usage : variable.valueUsages()) {
                        ArrayList<IElement> variants = new AssignmentFlow(usage).getVariants(usage);
                        InlineUsage inlineUsage = new InlineUsage(usage);
                        for (IElement variant : variants) {
                            // TODO default variant is to leave it as is if there is a conflict
                            inlineUsage.add(variant);
                            if (variant.equals(variable.getParent())) {
                                inlineAssignment.add(inlineUsage);
                            }
                        }
                    }
                    // TODO now I must improve a tree to make in comfortable to work with
                    // TODO I must show conflicts for people to understand that they are conflicts
                    // TODO checkboxes to choose solution
                    // TODO hotkeys to work with them
                    // TODO I need to make things doable with controls emulating hotkeys just to show tooltips

                    AssignmentNode node = new AssignmentNode(project, inlineAssignment);
                    DefaultMutableTreeNode rootNode = node.createTreeNode();
                    // TODO I should not display a tree if there is no conflicts
                    createRefactoringTree(project, rootNode, "Inline " + variable.getText());
                    // TODO do the refactoring
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

    private void createRefactoringTree(Project project, DefaultMutableTreeNode root, String displayName) {
        ContentManager contentManager = getStreamlineToolWindow(project);
        Tree tree = new Tree(root);
        tree.setEditable(true);

        tree.setCellRenderer(new RadioNodePanel().createRenderer());
        tree.setCellEditor(new RadioNodePanel().createEditor());

        Content tab = contentManager.getFactory().createContent(tree, displayName, true);
        for (int i=0; i<tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        contentManager.addContent(tab);
        contentManager.setSelectedContent(tab);
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
        // TODO InlineLocalDialog

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
