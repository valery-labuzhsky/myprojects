package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created on 16.07.2020.
 *
 * @author unicorn
 */
public abstract class RefactoringToolPanelAction extends AnAction {
    protected RefactoringToolPanel getToolPanel(@NotNull AnActionEvent e) {
        Component component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        while (component != null && !(component instanceof RefactoringToolPanel)) {
            component = component.getParent();
        }
        return (RefactoringToolPanel) component;
    }
}
