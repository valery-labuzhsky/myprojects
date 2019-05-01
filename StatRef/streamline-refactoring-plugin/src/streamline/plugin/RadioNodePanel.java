package streamline.plugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RadioNodePanel extends NodePanel {

    private JRadioButton radioButton;

    public RadioNodePanel() {
        composePanel();
    }

    @Override
    @NotNull
    protected JComponent createComponent() {
        radioButton = new JRadioButton();
        radioButton.setOpaque(false);
        return radioButton;
    }

    public JRadioButton getRadioButton() {
        return radioButton;
    }
}
