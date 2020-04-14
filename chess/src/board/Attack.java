package board;

import java.util.HashSet;

/**
 * Created on 14.04.2020.
 *
 * @author ptasha
 */
public class Attack extends Waypoint {
    public final Waypoint through;

    public Attack(Piece piece, Square square, Waypoint through) {
        super(piece, square);
        this.through = through;
    }

    @Override
    protected HashSet<Waypoint> getSquareCache() {
        return (HashSet) square.attacks;
    }

    @Override
    protected HashSet<Waypoint> getPieceCache() {
        return (HashSet) piece.attacks;
    }

    @Override
    public String toString() {
        return piece.type.c + "" + piece.square.pair + through.square.pair + square.pair;
    }

    public static class Origin extends Waypoint.Origin {
        final Waypoint through;

        public Origin(Waypoint through) {
            super(through.piece, through.square);
            this.through = through;
        }

        @Override
        public Waypoint create(Square square) {
            return new Attack(piece, square, through);
        }
    }
}
