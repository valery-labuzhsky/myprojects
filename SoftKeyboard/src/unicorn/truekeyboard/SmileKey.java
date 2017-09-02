package unicorn.truekeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author unicorn
 */
public class SmileKey extends Key implements View.OnClickListener {
    public SmileKey(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public SmileKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public SmileKey(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public void onClick(View view) {
        if (getText().equals("ABC")) {
            keyboard.setLetters();
        } else {
            keyboard.sendText(getText());
        }
    }
}
