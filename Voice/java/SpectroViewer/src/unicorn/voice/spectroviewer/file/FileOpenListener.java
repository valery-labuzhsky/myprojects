package unicorn.voice.spectroviewer.file;

import unicorn.utils.Listeners;

/**
 * @author unicorn
 */
public interface FileOpenListener {
    public static class s extends Listeners<FileOpenListener> {}

    void opened(String file);
}
