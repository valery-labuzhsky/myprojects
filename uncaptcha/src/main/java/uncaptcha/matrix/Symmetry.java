package uncaptcha.matrix;

public enum Symmetry {
    NONE(Transmutation.NONE),
    TRANSPOSE(Transmutation.TRANSPOSE),
    X(Transmutation.REFLECT_X),
    Y(Transmutation.REFLECT_Y);

    public Transmutation t;

    Symmetry(Transmutation t) {
        this.t = t;
    }
}
