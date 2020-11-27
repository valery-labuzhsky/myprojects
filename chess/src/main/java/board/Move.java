package board;

import board.pieces.Piece;
import board.pieces.PieceType;
import board.situation.Analytics;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class Move extends Action {
    public final Square from;
    public final Square to;
    public final PieceType promotion;
    public Piece capture;

    public final String note;

    public Move(Square from, Square to) {
        this(from, to, null, null);
    }

    public Move(Square from, Square to, PieceType promotion) {
        this(from, to, promotion, null);
    }

    public Move(Square from, Square to, PieceType promotion, String note) {
        super(from.piece);
        this.from = from;
        this.to = to;
        this.promotion = promotion;
        this.note = note;
    }

    @Override
    protected Board board() {
        return this.from.board;
    }

    public void move() throws IllegalMoveException {
        legalCheck();
        makeMove();
        board().history.push(this, true);
        afterCheck();
    }

    protected void afterCheck() throws IllegalMoveException {
        Board board = board();
        for (Piece king : board.pieces.get(color())) {
            if (king.type == PieceType.King) {
                if (king.isInDanger()) {
                    board.undo();
                    throw new IllegalMoveException("check");
                }
                break;
            }
        }
    }

    protected void legalCheck() throws IllegalMoveException {
        if (piece == null) {
            throw new IllegalMoveException("no piece on " + this.from);
        }

        if (piece.canMove(from, to)) {
            throw new IllegalMoveException();
        }
    }

    @Override
    public void undoMove() {
        try {
            // TODO undo promotion
            Square from = this.to;
            if (from.piece == null) {
                throw new IllegalMoveException("no piece on " + this.to);
            }
            from.piece.makeMove(this.from);

            Piece piece = this.capture;
            if (piece != null) {
                piece.add(from);
            }
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void makeMove() {
        if (piece.square != from) {
            throw new IllegalStateException();
        }

        if (to.piece != null) {
            capture = to.piece;
            to.piece.remove();
        }

        piece.makeMove(to);
    }

    public int color() {
        return piece.color;
    }

    public Stream<Analytics> trap() {
        Stream<Analytics> stream;
        if (to.piece == null) {
            stream = Stream.of(to.scores.getExchange(piece.color).move(piece));
        } else {
            // TODO what then?
            stream = Stream.empty();
        }
        return stream;
    }

    @Override
    public String toString() {
        return "" + from.pair + to.pair + (promotion == null ? "" : promotion);
    }

    @Override
    public Logger getLogger() {
        return Logged.log(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Move move = (Move) o;
        return from.equals(move.from) &&
                to.equals(move.to) &&
                promotion == move.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotion);
    }
}
