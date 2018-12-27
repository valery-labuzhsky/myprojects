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
import statref.model.SElement;
import statref.model.SInitializer;
import statref.model.STraceContext;
import statref.model.idea.IAssignment;
import statref.model.idea.IElement;
import statref.model.idea.IVariable;

import java.util.HashMap;
import java.util.LinkedList;

public class SLInlineAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO I need to: inline last value set if it is determined.
        // TODO If it's not - conflict: variants: manual editing, use one of the values
        // TODO on inline I must escape level of expresssion, and unescape when appropriate

        // TODO should I write tests from the beginning? probably yes, but I need do it manually first to keep me interested

        // TODO when should I add refactoring tree view? probably after some refactorings are already implemented, I should just not forget to leave commments

        PsiReferenceExpression reference = getPsiElement(event, PsiReferenceExpression.class);
        if (reference != null) {
            IVariable variable = new IVariable(reference);

            Project project = getEventProject(event);
            // TODO create a test?
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // TODO check there is an initializer
                SInitializer initializer = variable.declaration();

                // TODO we must build full tree of contexts before deciding on inline options
                // TODO how this tree will look like?
                // TODO first we must build a line from very top context down to initializer
                // TODO then we must add more lines and colapse them when required
                // TODO we may build a tree first and collapse after
                // method -> set
                // method -> if -> then // TODO we must somehow understand that these 2 are from the same context
                // method -> if -> else
                // method -> set
                // TODO if there is no else, then there is no collapse
                // TODO how would I do it?
                // TODO create an execution trace
                // set -> if (then, else) -> set -> block (set)
                // TODO we must also trigger variable removal when there are no usages
                // TODO so we must create a list of executions

                Trace trace = new Trace(initializer);
                System.out.println(trace);

                HashMap<STraceContext, SInitializer> initializers = new HashMap<>();
                initializers.put(initializer.getTraceContext(), initializer);

                // TODO should I create a method here? getInitializers?
                for (IVariable usage : variable.usages()) {
                    IElement parent = usage.getParent();
                    if (parent instanceof IAssignment) {
                        SInitializer assignment = (SInitializer) parent;
                        if (assignment.before(variable) && assignment.after(initializer)) {
                            initializer = assignment;
                            // TODO we must also check if one context is before or after
                            // TODO how we'll check it?
                            // TODO we should complicate before implementation, it should take context into account
                            initializers.put(assignment.getTraceContext(), assignment);
                            System.out.println(new Trace(initializer));
                        }
                    }
                }

//                variable.replace(initializer.getInitializer()); // TODO uncomment me
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

    // TODO think about: removing STraceContext, inlining it somewhere, inlining it's methods, rethink context
    private static class Trace {
        private final LinkedList<SElement> trace = new LinkedList<>();

        public Trace(SElement element) {
            // TODO top element - method declaration
            // TODO can I go beyond statement - why not, but not now
            // TODO we can do any granularity here not need to filter anything out
            // TODO but we must stop at method level
            // TODO it's different level of filtration
            trace.add(element);
            element = element.getParent();
            while (element != null && isTraceElement(element)) {
                trace.addFirst(element);
                element = element.getParent();
            }
        }

        private boolean isTraceElement(SElement element) {
            // TODO is it above method?
            return true;
        }

        @Override
        public String toString() {
            return "Trace=" + trace;
        }
    }
}
