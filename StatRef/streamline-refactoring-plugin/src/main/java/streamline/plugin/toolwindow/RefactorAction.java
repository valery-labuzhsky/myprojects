package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

public class RefactorAction extends RefactoringToolPanelAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        RefactoringToolPanel panel = getToolPanel(e);
        WriteCommandAction.runWriteCommandAction(e.getProject(), panel.getRefactoring()::refactor);
        panel.close(e);
    }

}
