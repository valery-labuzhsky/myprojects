package streamline.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import icons.StudioIcons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RefactorAction extends AnAction {
    public RefactorAction() {
        super("Refactor", "Refactor", StudioIcons.Shell.Toolbar.RUN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        RefactoringToolWindow toolWindow = getToolWindow(e);
        WriteCommandAction.runWriteCommandAction(e.getProject(), toolWindow.getRefactoring()::refactor);
    }

    private RefactoringToolWindow getToolWindow(@NotNull AnActionEvent e) {
        Component component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        while (component!=null && !(component instanceof RefactoringToolWindow)) {
            component = component.getParent();
        }
        return (RefactoringToolWindow) component;
    }
}
