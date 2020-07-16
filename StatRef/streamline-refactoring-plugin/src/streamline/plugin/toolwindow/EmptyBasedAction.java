package streamline.plugin.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;

import javax.swing.*;

/**
 * Created on 16.07.2020.
 *
 * @author unicorn
 */
public abstract class EmptyBasedAction extends AnAction {
    public EmptyBasedAction(String text, String description, Icon icon) {
        super(text, description, icon);
        copyShortcutFromConfiguredAction();
    }

    private void copyShortcutFromConfiguredAction() {
        AnAction sourceAction = ActionManager.getInstance().getAction(getClass().getName());
        if (sourceAction != null) {
            copyShortcutFrom(sourceAction);
        }
    }
}
