package unicorn.truekeyboard;

/**
 * @author unicorn
 */
public class KeyAction {
    private final String text;
    private final Special special;

    public KeyAction(Special special) {
        this.special = special;
        this.text = special.getText();
    }

    public KeyAction(char c) {
        this.special = null;
        this.text = ""+c;
    }

    public Special getSpecial() {
        return special;
    }

    public String getText() {
        return text;
    }

    public boolean isSpecial() {
        return special!=null;
    }

    public boolean isDwa() {
        return isSpecial() && special.isDwa();
    }
}
