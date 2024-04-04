package uncaptcha.matrix;

import static uncaptcha.Uncaptcha.WHITE;

public class StrictFragment extends Fragment {
    public StrictFragment(Matrix base, int x0, int y0, int width, int height) {
        super(base, x0, y0, width, height);
    }

    @Override
    public int get(int x, int y) {
        if (x<0 || x >= getWidth() || y < 0 || y >= getHeight()) return WHITE;
        return super.get(x, y);
    }
}
