package streamline.plugin;

import com.intellij.ide.projectView.PresentationData;
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
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.Tree;
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SInitializer;
import statref.model.idea.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

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

                    // TODO how to show conflicts?
                    // TODO it probably should be a branch with conflict description
                    // TODO no such child if there is no conflict
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
        Content tab = contentManager.getFactory().createContent(new Tree(root), displayName, true);
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

    private abstract static class RefactoringNode<R extends Refactoring> extends ElementNode {
        protected final R refactoring;
        private SimpleNode[] children;

        public RefactoringNode(Project project, R refactoring) {
            super(project);
            this.refactoring = refactoring;
            update();
        }

        @Override
        public SimpleNode[] getChildren() {
            if (children == null) {
                children = createChildren();
            }
            return children;
        }

        @NotNull
        public abstract SimpleNode[] createChildren();
    }

    public interface Refactoring {
    }

    private static class InlineAssignment implements Refactoring {
        private final IVariable variable;
        private final ArrayList<InlineUsage> usages = new ArrayList<>(); // TODO it's list of children refactorings

        public InlineAssignment(IVariable variable) {
            this.variable = variable;
        }

        public void add(InlineUsage usage) {
            usages.add(usage);
        }
    }

    private static class InlineUsage implements Refactoring {
        private final IVariable usage;
        private final AssignmentVariants variants = new AssignmentVariants();

        public InlineUsage(IVariable usage) {
            this.usage = usage;
        }

        public void add(IElement variant) {
            variants.add(variant);
        }
    }

    private static class AssignmentVariants implements Refactoring {
        private final ArrayList<IElement> variants = new ArrayList<>();

        public void add(IElement variant) {
            variants.add(variant);
        }
    }

    private static class AssignmentNode extends RefactoringNode<InlineAssignment> {
        public AssignmentNode(Project project, InlineAssignment inlineAssignment) {
            super(project, inlineAssignment);
        }

        @Override
        protected PsiElement getPsiElement() {
            return refactoring.variable.getElement();
        }

        @Override
        @NotNull
        public SimpleNode[] createChildren() {
            return refactoring.usages.stream().map(ch -> new InlineUsageNode(myProject, ch)).toArray(SimpleNode[]::new);
        }
    }

    private static class InlineUsageNode extends RefactoringNode<InlineUsage> {
        public InlineUsageNode(Project project, InlineUsage refactoring) {
            super(project, refactoring);
        }

        @NotNull
        @Override
        public SimpleNode[] createChildren() {
            return refactoring.variants.variants.stream().map(variant -> new VariantElementNode(myProject, variant)).toArray(SimpleNode[]::new);
        }

        @Override
        protected PsiElement getPsiElement() {
            return refactoring.usage.getElement();
        }
    }

    private static class VariantElementNode extends ElementNode {
        private final IElement variant;

        public VariantElementNode(Project project, IElement variant) {
            super(project);
            this.variant = variant;
            update();
        }

        @Override
        protected PsiElement getPsiElement() {
            if (variant instanceof SInitializer) {
                return ((IInitializer) variant).getInitializer().getElement();
            }
            return variant.getElement();
        }

        @Override
        public SimpleNode[] getChildren() {
            return new SimpleNode[0];
        }
    }

    public abstract static class ElementNode extends SimpleNode {
        public ElementNode(Project project) {
            super(project);
        }

        @NotNull
        public DefaultMutableTreeNode createTreeNode() {
            return createTreeNode(this);
        }

        @NotNull
        public DefaultMutableTreeNode createTreeNode(SimpleNode node) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
            for (SimpleNode child : node.getChildren()) {
                treeNode.add(createTreeNode(child));
            }
            return treeNode;
        }

        protected abstract PsiElement getPsiElement();

        @Override
        protected void doUpdate() {
            PresentationData presentation = getTemplatePresentation();
            presentation.clearText();
            PsiStatement statement = PsiTreeUtil.getParentOfType(getPsiElement(), PsiStatement.class);
            String statementText = statement.getText();
            int statementStart = statement.getTextOffset();
            int elementStart = getPsiElement().getTextOffset();
            presentation.addText(statementText.substring(0, elementStart - statementStart), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            presentation.addText(getPsiElement().getText(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
            presentation.addText(statementText.substring(elementStart - statementStart + getPsiElement().getTextLength()), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }
    }
}
