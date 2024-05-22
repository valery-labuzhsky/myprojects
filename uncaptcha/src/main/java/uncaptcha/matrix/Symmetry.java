package uncaptcha.matrix;

import java.util.stream.Stream;

public enum Symmetry {
    NONE(Transmutation.NONE),
    TRANSPOSE(Transmutation.TRANSPOSE),
    X(Transmutation.REFLECT_X),
    Y(Transmutation.REFLECT_Y);

    public Transmutation t;

    Symmetry(Transmutation t) {
        this.t = t;
    }

    public Stream<Symmetry> stream() {
        return Stream.of(NONE, this);
    }
}
