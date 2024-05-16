package uncaptcha.matrix;

public enum Rotation {
    ZERO(Transmutation.NONE),
    RIGHT(Transmutation.ROTATE_RIGHT),
    AROUND(Transmutation.ROTATE_AROUND),
    LEFT(Transmutation.ROTATE_LEFT);

    public Transmutation t;

    Rotation(Transmutation t) {
        this.t = t;
    }

}
