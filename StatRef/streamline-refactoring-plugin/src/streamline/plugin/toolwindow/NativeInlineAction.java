package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 16.07.2020.
 *
 * @author unicorn
 */
public class NativeInlineAction extends RefactoringToolWindowAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO how do I know it's inline action I need?
        //  it depends on refactoring
        AnAction nativeAction = ActionManager.getInstance().getAction("Inline");
        ActionUtil.performActionDumbAware(nativeAction, getToolWindow(e).originalEvent);
    }
}
