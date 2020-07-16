package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

public class RefactorAction extends RefactoringToolWindowAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WriteCommandAction.runWriteCommandAction(e.getProject(), getToolWindow(e).getRefactoring()::refactor);
    }

}
