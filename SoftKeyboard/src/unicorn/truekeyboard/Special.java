package unicorn.truekeyboard;

/**
 * @author unicorn
 */
public enum Special {
    BS("\u2190"),
    SPACE(" ", true),
    ENTER("\u21B5", true),
    SHIFT("\u21EA"),
    LANG("Lang", true),
    NUMBERS("?123", true),
    LETTERS("ABC", true),
    SMILES(":-)", true),
    ELLIPSIS("...");

    private final String text;
    private final boolean dwa;

    private Special(String text) {
        this(text, false);
    }

    private Special(String text, boolean dwa) {
        this.text = text;
        this.dwa = dwa;
    }

    public boolean isDwa() {
        return dwa;
    }

    public String getText() {
        return text;
    }
}
