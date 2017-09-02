package unicorn.truekeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * @author unicorn
 */
public abstract class Key extends Button {
    public Key(Context context) {
        super(context);
    }

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Key(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected TrueKeyboard keyboard;

    public void setKeyboard(TrueKeyboard keyboard) {
        this.keyboard = keyboard;
    }
}
