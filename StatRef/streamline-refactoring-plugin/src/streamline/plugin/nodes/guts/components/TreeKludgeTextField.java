package streamline.plugin.nodes.guts.components;

import javax.swing.*;

public class TreeKludgeTextField extends JTextField {
    @Override
    public void selectAll() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (trace.length > 2 && "removeFromSource".equals(trace[2].getMethodName())) {
            return;
        }
        super.selectAll();
    }
}
