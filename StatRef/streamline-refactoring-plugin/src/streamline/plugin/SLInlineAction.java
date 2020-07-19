package streamline.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.*;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.refactoring.InlineAssignment;
import streamline.plugin.refactoring.InlineParameter;
import streamline.plugin.refactoring.InlineUsage;
import streamline.plugin.refactoring.InlineVariable;
import streamline.plugin.toolwindow.RefactoringToolPanel;

public class SLInlineAction extends AnAction {
    private static final Logger log = Logger.getInstance(SLInlineAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        boolean invokeNative = true;
        try {
            PsiIdentifier identifier = getPsiElement(event, PsiIdentifier.class);
            if (identifier == null) return;
            IElement entity = IFactory.getElement(identifier.getParent());
            Project project = getEventProject(event);
            NodesRegistry registry = new NodesRegistry(project);
            if (entity instanceof ILocalVariableDeclaration) {
                ILocalVariableDeclaration declaration = (ILocalVariableDeclaration) entity;
                RefactoringToolPanel toolWindow = new RefactoringToolPanel(event, "Inline " + declaration.getText());
                InlineVariable refactoring = registry.getRefactorings().getRefactoring(new InlineVariable(declaration, registry.getRefactorings()));
                toolWindow.setRoot(registry.create(refactoring));
                invokeNative = false;
            } else if (entity instanceof ILocalVariable) {
                // TODO I cannot differentiate between fields and local variables
                //  I need more elaborate things
                //  sometimes I can other times I don't
                //  so I need something to address it
                //  I need some common class where I can pass my expression and have method to check it's true nature
                //  I may need it not only with variables but with any element
                //  variable are part of a language though independent of implementation

                // TODO I need some refactoring of Variables: Field is a Variable
                //  Local variable is a different beast
                //  I will also have a variable of unknown nature
                // TODO I must also use factory for everything
                ILocalVariable variable = (ILocalVariable) entity;

                RefactoringToolPanel toolWindow = new RefactoringToolPanel(event, "Inline " + variable.getName());

                if (variable.isAssignment()) {
                    InlineVariable inlineVariable = registry.getRefactorings().getRefactoring(new InlineVariable(variable.declaration(), registry.getRefactorings()));
                    InlineAssignment assignment = inlineVariable.enableOnly((IInitializer) variable.getParent());
                    toolWindow.setRoot(registry.create(inlineVariable)).select(assignment);
                } else {
                    InlineVariable inlineVariable = registry.getRefactorings().getRefactoring(new InlineVariable(variable.declaration(), registry.getRefactorings()));
                    InlineUsage usage = inlineVariable.enableOnly(variable);
                    toolWindow.setRoot(registry.create(inlineVariable)).select(usage);
                }
                invokeNative = false;
            } else if (entity instanceof IParameter) {
                IParameter parameter = (IParameter) entity;

                InlineParameter refactoring = new InlineParameter(registry, parameter);

                RefactoringToolPanel tree = new RefactoringToolPanel(event, "Inline " + parameter.getName());
                tree.setRoot(registry.create(refactoring));
                invokeNative = false;
            }
        } catch (RuntimeException e) {
            log.error(e);
        } finally {
            if (invokeNative) invokeNative(event);
        }
    }

    public void invokeNative(@NotNull AnActionEvent event) {
        ActionManager.getInstance().getAction("Inline").actionPerformed(event);
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
