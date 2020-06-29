package board.situation;

import board.pieces.Piece;
import board.pieces.ScoreProvider;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public class PieceScore implements ScoreProvider {
    private final Piece piece;
    private final Function<Piece, Analytics> score;

    public static ScoreWatcher diff(Piece piece, Function<Piece, Analytics> score) {
        return new ScoreWatcher(new PieceScore(piece, score));
    }

    private PieceScore(Piece piece, Function<Piece, Analytics> score) {
        this.piece = piece;
        this.score = score;
    }

    @Override
    public Analytics analyse() {
        Analytics analytics;
        if (piece.onBoard()) {
            analytics = calculateScore();
        } else {
            analytics = new TakenPiece(this.piece);
        }
        return analytics;
    }

    protected Analytics calculateScore() {
        return score.apply(this.piece);
    }

    @Override
    public String toString() {
        return piece.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceScore score = (PieceScore) o;
        return piece.equals(score.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece);
    }

}
