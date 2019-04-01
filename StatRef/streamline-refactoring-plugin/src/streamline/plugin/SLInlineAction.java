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
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SInitializer;
import statref.model.idea.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        if (reference != null) {
            IVariable variable = new IVariable(reference);

            Project project = getEventProject(event);
            // TODO create a test?
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // TODO do we need to add it to PSI? why not? but not right now
                if (variable.isAssignment()) {
                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("streamline-toolwindow", true, ToolWindowAnchor.RIGHT);
                    ContentManager contentManager = toolWindow.getContentManager();
                    Content content = contentManager.getFactory().createContent(new JLabel("Test"), "Test", true);
                    contentManager.addContent(content);

                    for (IVariable mention : variable.mentions()) {
                        // TODO how to check if something is before our mention?
                        // TODO 1. find common block, get text mentions of childs
                        // TODO 2. in cycles anyhing can go before anything, but only if variable is defined outside of the cycle
                        // TODO 3. if the most common parent is if, they are in parallel branches
                        // TODO so I must do it in AssignmentFlow

                        // TODO so I simply need to check what is after this assignment and next
                        // TODO or I can find assignnments for variables
                        // TODO I must show conflicts for all the variables
                        // TODO so it just massive inline usage thing
                        // TODO all I need to do is to exclude
                        // TODO I must create conflicts panel!
                    }
                    // TODO usage flow
                    // TODO remove assignment
                } else {
                    AssignmentFlow flow = new AssignmentFlow(variable);
                    ArrayList<IElement> variants = flow.getVariants(variable);
                    SInitializer initializer = null;
                    if (variants.size() == 0) {
                        // TODO show error
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

    private static class AssignmentFlow {
        private final IElement top;
        private final HashMap<IElement, List<IElement>> variables = new HashMap<>();

        public AssignmentFlow(IVariable variable) {
            IElement declaration = variable.declaration();
            top = declaration.getParent().getParent(); // TODO may not work for every case
            add(declaration);
            for (IVariable usage : variable.mentions()) {
                if (usage.isAssignment()) {
                    add(usage.getParent());
                }
            }
        }

        private void add(IElement assignment) {
            add(assignment.getParent(), assignment);
        }

        private void add(IElement context, IElement assignment) {
            if (context instanceof IIfStatement && conditional((IIfStatement) context, assignment)) {
                assignment = context;
            } else {
                variables.computeIfAbsent(context, key -> new ArrayList<>()).add(assignment);
            }
            if (!context.equals(top)) {
                add(context.getParent(), assignment);
            }
        }

        private boolean conditional(IIfStatement context, IElement assignment) { // TODO create a class/methods for it
            return conditional(context.getThenBranch(), assignment) || conditional(context.getElseBranch(), assignment);
        }

        private boolean conditional(IStatement branch, IElement assignment) {
            return branch != null && branch.contains(assignment);
        }

        public ArrayList<IElement> getVariants(IElement usage) {
            ArrayList<IElement> variants = new ArrayList<>();
            IElement context = usage;
            do {
                context = context.getParent();
            } while (!getVariants(usage, context, variants) && !context.equals(top));
            return variants;
        }

        public boolean getVariants(IElement usage, IElement context, ArrayList<IElement> variants) {
            List<IElement> elements = variables.get(context);
            if (elements != null) {
                for (ListIterator<IElement> iterator = elements.listIterator(elements.size()); iterator.hasPrevious(); ) {
                    IElement element = iterator.previous();
                    if (usage == null || element.before(usage)) { // TODO will it work all the time? like in cycles
                        if (element instanceof IIfStatement) {
                            IIfStatement ifStatement = (IIfStatement) element;
                            if (getVariants(null, ifStatement.getElseBranch(), variants) &
                                    getVariants(null, ifStatement.getThenBranch(), variants)) {
                                return true;
                            }
                        } else {
                            variants.add(element);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
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
                    return super.getListCellRendererComponent(list, ((SInitializer)value).getInitializer().getText(), index, isSelected, cellHasFocus);
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
