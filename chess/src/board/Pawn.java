package board;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Pawn extends Piece {
    public Pawn(Board board, int color) {
        super(PieceType.Pawn, board, color);
    }

    @Override
    protected void marksOn() {
        Mark first = mark(null, 0, color);
        mark(-1, color);
        mark(1, color);
        // enpassant
        int border = (7 - color * 7) / 2;
        int secondRow = border + color;
        if (square.pair.rank == secondRow) {
            mark(first, 0, color);
        }
    }

    @Override
    protected Move move(Mark mark) {
        if (mark.square.pair.file == square.pair.file) {
            if (mark.square.piece != null) {
                return null;
            }
        } else {
            // TODO enpassant
            if (mark.square.piece == null) {
                return null;
            }
        }
        return super.move(mark);
    }

    @Override
    public boolean captures(Mark mark) {
        return mark.square.pair.file != square.pair.file;
    }
}
