package streamline.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.assignment.AssignmentNode;
import streamline.plugin.refactoring.assignment.InlineAssignment;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

public class SLInlineAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO on inline I must escape level of expression, and unescape when appropriate

        // TODO should I write tests from the beginning? probably yes, but I need do it manually first to keep me interested

        // TODO when should I add refactoring tree view? probably after some refactorings are already implemented, I should just not forget to leave comments
        // TODO the tests are hard to run, and they don't work anyway
        // TODO I may also try idea's tests

        // TODO it doesn't work with declaration
        // TODO technically I don't have reference here, as I have a declaration
        // TODO but I need the same algorithm to work with it
        // TODO what keeps me from thinking of declaration as a reference to itself?
        // TODO I would need an common interface/class which does it
        // TODO I have Initializer for assignment and declaration
        // TODO what would I call for initializer and usage? reference?
        PsiIdentifier identifier = getPsiElement(event, PsiIdentifier.class);
        PsiElement parent = identifier.getParent();
        Project project = getEventProject(event);
        if (parent instanceof PsiLocalVariable) {
            IInitializer declaration = new IVariableDeclaration((PsiLocalVariable) parent);
            InlineAssignment refactoring = new InlineAssignment(declaration);
            // TODO now I must improve a tree to make in comfortable to work with
            // TODO I need to make things doable with controls emulating hotkeys just to show tooltips
            // TODO I need to offer ability to inline all assignments
            createRefactoringTree(project, "Inline " + declaration.getText(), new AssignmentNode(project, refactoring));
        } else if (parent instanceof PsiReferenceExpression) {
            IVariable variable = new IVariable((PsiReferenceExpression) parent);
            if (variable.isAssignment()) {
                IInitializer assignment = (IInitializer) variable.getParent();
                InlineAssignment refactoring = new InlineAssignment(assignment);
                createRefactoringTree(project, "Inline " + assignment.getText(), new AssignmentNode(project, refactoring));
            } else {
                InlineUsageNode node = new InlineUsageNode(project, new InlineUsage(variable));
                createRefactoringTree(project, "Inline "+variable.getName(), node);
                // TODO no it's better to use the same assignment node
                // TODO but what to do if there is many of them?
                // TODO starting with what is intended is the most obvious thing
                // TODO I need to make already mentioned nodes unmodifiable
                // TODO but I don't need to make recursion
                // TODO I don't need to change refactoing itself, I need to do it on Node level

                // TODO I can just hide what's unintended
                // TODO I can show choise every time, but choose and chow only what I think fits best
                // TODO if I'm wrong all you need to do is to choose another option
                // TODO let it be!

                // TODO why I don't want to do assignment as child of usage?
                // TODO it's not intuitive to see assignment below usage, for me
            }
        } else {
            // TODO try to run default refactoring
        }
    }

    private void createRefactoringTree(Project project, String displayName, RefactoringNode node) {
        ContentManager contentManager = getStreamlineToolWindow(project);

        RefactoringToolWindow toolWindow = new RefactoringToolWindow(node);

        Content tab = contentManager.getFactory().createContent(toolWindow, displayName, true);
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
            P element = PsiTreeUtil.getParentOfType(file.findElementAt(offset), aClass, false);
            if (element == null) {
                return PsiTreeUtil.getParentOfType(file.findElementAt(offset - 1), aClass, false);
            }
            return element;
        }
        return null;
    }
}
