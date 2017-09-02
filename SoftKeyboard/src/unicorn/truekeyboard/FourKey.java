package unicorn.truekeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * @author unicorn
 */
public class FourKey extends Key implements View.OnTouchListener {
    public FourKey(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public FourKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public FourKey(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    private List<KeyAction> keys;

    private float x;
    private float y;

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            x = motionEvent.getX();
            y = motionEvent.getY();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            int i = 0;
            if (motionEvent.getX() >= x) i += 1;
            if (motionEvent.getY() >= y) i += 2;
            KeyAction keyAction = keys.get(i);
            if (keyAction.isSpecial()) {
                switch (keyAction.getSpecial()) {
                    case BS:
                        keyboard.backspace();
                        break;
                    case ENTER:
                        keyboard.sendText("\n");
                        break;
                    case LANG:
                        keyboard.switchLanguage();
                        break;
                    case NUMBERS:
                        keyboard.setNumbers();
                        break;
                    case LETTERS:
                        keyboard.setLetters();
                        break;
                    case SHIFT:
                        keyboard.switchShift();
                        break;
                    case SMILES:
                        keyboard.setSmiles();
                        break;
                    case SPACE:
                        keyboard.sendText(" ");
                        break;
                    case ELLIPSIS:
                        keyboard.sendText("...");
                        break;
                }
            } else {
                String text = keyAction.getText();
                if (keyboard.isShift()) {
                    text = text.toUpperCase();
                }
                keyboard.sendText(text);
            }
        }
        return true;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        keys = new ArrayList<KeyAction>();
        int state = 0;
        String special = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (state) {
                case 0:
                    if (c == '#') {
                        state = 1;
                        special = "";
                    } else {
                        keys.add(new KeyAction(c));
                    }
                    break;
                case 1:
                    if (c == ';') {
                        if (special.equals("")) {
                            keys.add(new KeyAction('#'));
                        } else {
                            KeyAction action = new KeyAction(Special.valueOf(special));
                            keys.add(action);
                            if (action.getSpecial().isDwa()) {
                                keys.add(action);
                            }
                        }
                        state = 0;
                    } else {
                        special += c;
                    }
                    break;
            }
        }
        super.setText("", type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Rect bounds = new Rect();
        paint.getTextBounds("I", 0, 1, bounds);
        int height = bounds.height();

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                KeyAction keyAction = keys.get(y * 2 + x);
                String text = keyAction.getText();
                if (!keyAction.isSpecial() && keyboard.isShift()) {
                    text = text.toUpperCase();
                }
                paint.getTextBounds(text, 0, text.length(), bounds);
                int width = bounds.width();
                if (keyAction.isDwa()) {
                    canvas.drawText(text, getWidth() / 2 - width / 2, y * getHeight() / 2 + getHeight() / 4 + height / 2, paint);
                    break;
                } else {
                    canvas.drawText(text, x * getWidth() / 2 + getWidth() / 4 - width / 2, y * getHeight() / 2 + getHeight() / 4 + height / 2, paint);
                }
            }
        }
    }
}
