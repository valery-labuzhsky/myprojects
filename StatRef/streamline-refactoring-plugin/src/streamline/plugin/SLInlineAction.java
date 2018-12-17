package streamline.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import statref.model.SInitializer;
import statref.model.idea.IAssignment;
import statref.model.idea.IElement;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;

public class SLInlineAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO I need some examples, what examples whould I use? just come up with random examples?
        // TODO I need to: inline last value set if it is determined.
        // TODO If it's not - conflict: variants: manual editing, use one of the values
        // TODO on inline I must escape level of expresssion, and unescape when appropriate
        // TODO it's more than enough to start

        // TODO should I write tests from the beginning? probably yes, but I need do it manually first to keep me interested

        // TODO write inline in IDEA tearms firts, replace it with my model then
        // TODO find PsiIdentifier
        // TODO find its definition (value set)
        // TODO replace it with definition
        // TODO when should I add complexity? probably after replacing with my model
        // TODO when should I add refactoring tree view? probably after some refactorings are already implemented, I should just not forget to leave commments

        PsiReferenceExpression reference = getPsiElement(event, PsiReferenceExpression.class);
        if (reference != null) {
            IVariable variable = new IVariable(reference);

            Project project = getEventProject(event);
            // TODO create a test?
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // TODO check there is an initializer
                SInitializer initializer = variable.declaration();

                // TODO should I create a method here? getInitializers?
                for (IVariable usage : variable.usages()) {
                    IElement parent = usage.getParent();
                    if (parent instanceof IAssignment) {
                        IAssignment assignment = (IAssignment) parent;
                        if (assignment.before(variable) && assignment.after(initializer)) {
                            // TODO check context
                            // TODO I should find last assignment in every context, except for simple contexts
                            // TODO what is context? it's a block where it used, or class if for a field,
                            // TODO it may happen in if without any brakets
                            initializer = assignment;
                            System.out.println(initializer.getContext());
                        }
                    }
                }

//                variable.replace(initializer.getInitializer());
            });

            // TODO make it finally mine?
            // TODO how to do it?
            // TODO from back to end
            // TODO I need a mean to create my object out of idea ones
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

}
