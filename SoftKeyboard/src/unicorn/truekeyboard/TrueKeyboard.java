package unicorn.truekeyboard;

import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ViewFlipper;
import com.example.android.softkeyboard.R;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author unicorn
 */
public class TrueKeyboard extends InputMethodService {

    private ViewFlipper view;

    @Override
    public View onCreateInputView() {
        view = (ViewFlipper) getLayoutInflater().inflate(R.layout.layout, null);
        for (int i=0; i< view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof KeyboardView) {
                KeyboardView keyboard = (KeyboardView) child;
                for (int j=0; j<keyboard.getChildCount(); j++) {
                    View keyboardChild = keyboard.getChildAt(j);
                    if (keyboardChild instanceof Key) {
                        Key key = (Key) keyboardChild;
                        key.setKeyboard(this);
                    }
                }
            }
        }
        if (display==Display.LETTERS) setLetters();
        return view;
    }

    public void sendText(CharSequence text) {
        getCurrentInputConnection().commitText(text, 1);
        resetShift();
    }

    public void backspace() {
        keyDownUp(KeyEvent.KEYCODE_DEL);
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private boolean shift;

    public void switchShift() {
        shift = !shift;
        view.invalidate();
    }

    public void resetShift() {
        shift = false;
        view.invalidate();
    }

    public boolean isShift() {
        return shift;
    }

    private Language language = Language.RUSSIAN;

    public void switchLanguage() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        IBinder token = getCurrentInputBinding().getConnectionToken();
        manager.switchToNextInputMethod(onBind(null), true);
    }

    @Override
    protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype newSubtype) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype);
        String lang = newSubtype.getExtraValueOf("lang");
        language = Language.valueOf(lang.toUpperCase());
        if (display==Display.LETTERS) setLetters();
    }

    public void setLetters() {
        view.setDisplayedChild(language.getNumber());
        display = Display.LETTERS;
    }

    public void setNumbers() {
        view.setDisplayedChild(2);
        display = Display.NUMBERS;
    }

    public void setSmiles() {
        view.setDisplayedChild(3);
        display = Display.SMILES;
    }

    private Display display = Display.LETTERS;

    private static enum Display {
        LETTERS,
        NUMBERS,
        SMILES
    }

}
