package unicorn.voice.spectroviewer.utils;

import unicorn.utils.Preferences;

/**
 * @author unicorn
 */
public enum PrefEnum {
    REOPEN,
    FOLDER;

    public final static Preferences<PrefEnum> PREFERENCES = new Preferences<PrefEnum>("voice.conf");
}
