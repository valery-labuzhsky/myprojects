package board;

import board.pieces.Piece;

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
    public Square getOriginalSquare() {
        return through.square;
    }

    public boolean isBlocking(Waypoint waypoint) {
        return waypoint.prev != null && this.prev != null && waypoint.prev.square == this.prev.square;
    }

    public int getScore() {
        int score = through.getScore();
        if (square.piece != null) {
            return -new AttackExchange(this).getScore() * square.piece.color * piece.color + score;
        }
        return score;
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
            for (Waypoint waypoint : this.through.square.waypoints) {
            }
        }

        @Override
        public Waypoint create(Square square) {
            return new Attack(piece, square, through);
        }
    }
}
