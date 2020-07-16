package streamline.plugin.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

public class RefactorAction extends RefactoringToolWindowAction {
    public RefactorAction() {
        super("Refactor", "Refactor", AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WriteCommandAction.runWriteCommandAction(e.getProject(), getToolWindow(e).getRefactoring()::refactor);
    }

}
