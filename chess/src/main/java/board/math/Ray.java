package board.math;

import board.Square;

import java.util.stream.Stream;

import static java.lang.Math.abs;

/**
 * Created on 28.11.2020.
 *
 * @author unicorn
 */
public enum Ray {
    UP(new Pair(0, 1)),
    UP_RIGHT(new Pair(1, 1)),
    RIGHT(new Pair(1, 0)),
    DOWN_RIGHT(new Pair(1, -1)),
    DOWN(new Pair(0, -1)),
    DOWN_LEFT(new Pair(-1, -1)),
    LEFT(new Pair(-1, 0)),
    UP_LEFT(new Pair(-1, 1));

    public final Pair r;

    Ray(Pair r) {
        this.r = r;
    }

    public static Stream<Ray> stream() {
        return Stream.of(values());
    }

    public static Stream<Stream<Square>> rays(Square square) {
        return stream().map(r -> r.ray(square));
    }

    public Stream<Square> ray(Square square) {
        return square.ray(this.r.file, this.r.rank);
    }

    public int distance(Square from, Square to) {
        return (abs(from.pair.file - to.pair.file) + abs(from.pair.rank - to.pair.rank)) / (abs(r.file) + abs(r.rank));
    }
}
