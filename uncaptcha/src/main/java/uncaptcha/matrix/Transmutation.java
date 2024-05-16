package uncaptcha.matrix;

public interface Transmutation {
    Transmutation NONE = new Transmutation() {
    };
    ReflectX REFLECT_X = new ReflectX();
    ReflectY REFLECT_Y = new ReflectY();
    Transpose TRANSPOSE = new Transpose();
    Transmutation ROTATE_RIGHT = TRANSPOSE.then(REFLECT_Y);
    Transmutation ROTATE_AROUND = REFLECT_Y.then(REFLECT_X);
    Transmutation ROTATE_LEFT = TRANSPOSE.then(REFLECT_X);

    default int transmuteX(int x, int y, Matrix matrix) {
        return x;
    }

    default int transmuteY(int x, int y, Matrix matrix) {
        return y;
    }

    default Transmutation back() {
        return this;
    }

    default Transmutation then(Transmutation then) {
        Transmutation first = this;
        return new Transmutation() {
            @Override
            public int transmuteX(int x, int y, Matrix matrix) {
                int x1 = first.transmuteX(x, y, matrix);
                int y1 = first.transmuteY(x, y, matrix);
                return then.transmuteX(x1, y1, matrix);
            }

            @Override
            public int transmuteY(int x, int y, Matrix matrix) {
                int x1 = first.transmuteX(x, y, matrix);
                int y1 = first.transmuteY(x, y, matrix);
                return then.transmuteY(x1, y1, matrix);
            }

            @Override
            public Transmutation back() {
                return then.back().then(first);
            }
        };
    }

    default Matrix mute(Matrix m) {
        return new Transmuted(m, this);
    }
}
