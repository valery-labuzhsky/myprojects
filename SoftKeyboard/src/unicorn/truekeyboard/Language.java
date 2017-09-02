package unicorn.truekeyboard;

/**
 * @author unicorn
 */
public enum Language {
    ENGLISH(0),
    RUSSIAN(1);

    private final int number;

    private Language(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
