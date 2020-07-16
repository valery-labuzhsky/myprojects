package streamline.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.*;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.refactoring.InlineAssignment;
import streamline.plugin.refactoring.InlineParameter;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.InlineVariable;
import streamline.plugin.toolwindow.RefactoringToolWindow;

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
            NodesRegistry registry = new NodesRegistry(project);
            if (parent instanceof PsiLocalVariable) {
                IVariableDeclaration declaration = new IVariableDeclaration((PsiLocalVariable) parent);
                RefactoringToolWindow toolWindow = createTree(project, event, "Inline " + declaration.getText());
                InlineVariable refactoring = registry.getRefactorings().getRefactoring(new InlineVariable(declaration, registry.getRefactorings()));
                toolWindow.setRoot(registry.create(refactoring));
                // TODO now I must improve a tree to make in comfortable to work with
                // TODO I need to make things doable with controls emulating hotkeys just to show tooltips
//                InlineAssignment refactoring = registry.getRefactorings().getRefactoring(new InlineAssignment(registry.getRefactorings(), declaration).selectDefaultVariant());
//                createRefactoringTree(project, event, "Inline " + declaration.getText(), new InlineAssignmentNode(refactoring, registry));
            } else if (parent instanceof PsiReferenceExpression) {
                IVariable variable = new IVariable((PsiReferenceExpression) parent);

                RefactoringToolWindow toolWindow = createTree(project, event, "Inline " + variable.getName());

                if (variable.isAssignment()) {
                    InlineVariable inlineVariable = registry.getRefactorings().getRefactoring(new InlineVariable(variable.declaration(), registry.getRefactorings()));
                    InlineAssignment assignment = inlineVariable.enableOnly((IInitializer) variable.getParent());
                    toolWindow.setRoot(registry.create(inlineVariable)).select(assignment);
                } else {
                    InlineVariable inlineVariable = registry.getRefactorings().getRefactoring(new InlineVariable(variable.declaration(), registry.getRefactorings()));
                    InlineUsage usage = inlineVariable.enableOnly(variable);
                    toolWindow.setRoot(registry.create(inlineVariable)).select(usage);
                }
            } else if (parent instanceof PsiParameter) {
                IParameter parameter = IFactory.getElement(parent);

                InlineParameter refactoring = new InlineParameter(registry, parameter);

                RefactoringToolWindow tree = createTree(project, event, "Inline " + parameter.getName());
                tree.setRoot(registry.create(refactoring));
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

}
