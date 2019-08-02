package streamline.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import statref.model.builder.BMethod;
import statref.model.builder.BMethodDeclaration;
import statref.model.builder.BVariable;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;
import streamline.plugin.refactoring.assignment.AssignmentNode;
import streamline.plugin.refactoring.assignment.InlineAssignment;
import streamline.plugin.refactoring.compound.CompoundRefactoring;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SLInlineAction extends AnAction {
    private static final Logger log = Logger.getInstance(IFactory.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            // TODO should I write tests from the beginning? probably yes, but I need do it manually first to keep me interested

            // TODO the tests are hard to run, and they don't work anyway
            // TODO I may also try idea's tests
            // TODO concept is constantly changing right know - I will need tests when it settles down

            PsiIdentifier identifier = getPsiElement(event, PsiIdentifier.class);
            PsiElement parent = identifier.getParent();
            Project project = getEventProject(event);
            NodesRegistry registry = new NodesRegistry();
            if (parent instanceof PsiLocalVariable) {
                IInitializer declaration = new IVariableDeclaration((PsiLocalVariable) parent);
                InlineAssignment refactoring = new InlineAssignment(registry.getRefactorings(), declaration).selectDefaultVariant();
                // TODO now I must improve a tree to make in comfortable to work with
                // TODO I need to make things doable with controls emulating hotkeys just to show tooltips
                createRefactoringTree(project, event, "Inline " + declaration.getText(), new AssignmentNode(project, refactoring, registry));
            } else if (parent instanceof PsiReferenceExpression) {
                IVariable variable = new IVariable((PsiReferenceExpression) parent);

                RefactoringToolWindow toolWindow = createTree(project, event, "Inline " + variable.getName());

                if (variable.isAssignment()) {
                    InlineAssignment refactoring = new InlineAssignment(registry.getRefactorings(), (IInitializer) variable.getParent());
                    AssignmentNode node = (AssignmentNode) createNode(project, toolWindow, refactoring, registry);
                } else {
                    InlineUsage refactoring = registry.getRefactorings().getRefactoring(new InlineUsage(variable, registry.getRefactorings()));
                    InlineUsageNode node = (InlineUsageNode) createNode(project, toolWindow, refactoring, registry);
                    node.selectAny();
                }
            } else if (parent instanceof PsiParameter) {
                IParameter parameter = IFactory.getElement(parent);
                IMethodDeclaration method = parameter.getParent();
                ArrayList<IMethodCall> calls = method.getCalls();
                Map<Object, List<IExpression>> expressions = new HashMap<>();
                for (IMethodCall call : calls) {
                    IExpression expression = call.getExpression(parameter);
                    expressions.computeIfAbsent(expression.signature(), (k) -> new ArrayList<>()).add(expression);
                }

                CompoundRefactoring refactoring = new CompoundRefactoring(registry.getRefactorings());
                int i = 0;
                for (List<IExpression> value : expressions.values()) {
                    BMethodDeclaration prototype = new BMethodDeclaration(method.getName() + i++) {
                        @Override
                        public void describe() {
                            BMethod delegate = new BMethod(null, method.getName());
                            for (IParameter param : method.getParameters()) {
                                if (param.equals(parameter)) {
                                    delegate.getParams().add(value.get(0));
                                } else {
                                    delegate.getParams().add(new BVariable(param));
                                }
                            }
                            if (method.isVoid()) {
                                code(delegate);
                            } else {
                                returnType(method.getReturnType());
                                return_(delegate);
                            }
                        }
                    }.returnType(method.getReturnType());

                    IMethodDeclaration newMethod = IFactory.convertMethodDeclaration(method.getProject(), prototype);

                    System.out.println(newMethod.getText());

                    CreateMethod createMethod = new CreateMethod(registry.getRefactorings(), method, newMethod);
                    // TODO add me to a tree
                }

                WriteCommandAction.runWriteCommandAction(project, refactoring::refactor);

                System.out.println(expressions.values());
                // TODO now let's create a refactoring tree
                // TODO 1. create method for every signature (but not each of them probably, we need to balance between gains and losses)
                // TODO 2. replace method calls with new method calls
                // TODO 3. take care of parameters
            } else {
                // TODO do not need to catch Exception from here
                invokeNative(event);
            }
        } catch (RuntimeException e) {
            log.error(e);
            invokeNative(event);
        }
    }

    public void invokeNative(@NotNull AnActionEvent event) {
        ActionManager.getInstance().getAction("Inline").actionPerformed(event);
    }

    public RefactoringNode createNode(Project project, RefactoringToolWindow toolWindow, Refactoring r, NodesRegistry registry) {
        RefactoringNode n = RefactoringNode.create(project, r, registry);
        toolWindow.setNode(n);
        return n;
    }

    private RefactoringToolWindow createRefactoringTree(Project project, AnActionEvent event, String displayName, RefactoringNode node) {
        RefactoringToolWindow toolWindow = createTree(project, event, displayName);
        toolWindow.setNode(node);
        return toolWindow;
    }

    @NotNull
    private RefactoringToolWindow createTree(Project project, AnActionEvent event, String displayName) {
        ContentManager contentManager = getStreamlineToolWindow(project);

        RefactoringToolWindow toolWindow = new RefactoringToolWindow(event);

        Content tab = contentManager.getFactory().createContent(toolWindow, displayName, true);
        contentManager.addContent(tab);
        contentManager.setSelectedContent(tab);
        toolWindow.getTree().requestFocusInWindow();
        return toolWindow;
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
            P element = PsiTreeUtil.getParentOfType(file.findElementAt(offset), aClass, false);
            if (element == null) {
                return PsiTreeUtil.getParentOfType(file.findElementAt(offset - 1), aClass, false);
            }
            return element;
        }
        return null;
    }

    public static class CreateMethod extends Refactoring {
        private final IMethodDeclaration after;
        private final IMethodDeclaration method;

        public CreateMethod(RefactoringRegistry registry, IMethodDeclaration after, IMethodDeclaration method) {
            super(registry);
            this.after = after;
            this.method = method;
        }

        @Override
        protected void doRefactor() {
            PsiMethod anchor = after.getElement();
            anchor.getParent().addAfter(method.getElement(), anchor);
        }
    }

}
