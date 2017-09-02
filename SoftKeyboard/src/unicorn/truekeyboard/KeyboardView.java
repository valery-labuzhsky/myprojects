package unicorn.truekeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author unicorn
 */
public class KeyboardView extends ViewGroup {
    public KeyboardView(Context context) {
        super(context);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
//        for (int i=0; i<6*2; i++) {
//            addView(new Key(getContext()));
//        }
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                layoutChild(i, left, top, right, bottom);
            }
        }
    }

    private void layoutChild(int i, int left, int top, int right, int bottom) {
        int row = i / 6;
        int col = i % 6;
        int rowSize = (bottom - top) / 2;
        int colSize = (right - left) / 6;
        int l = left + col * colSize;
        int t = top + row * rowSize;
        getChildAt(i).layout(l, t, l + colSize, t + rowSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size*2/6);
    }
}
