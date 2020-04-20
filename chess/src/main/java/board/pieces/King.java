package board.pieces;

import board.MovesTracer;
import board.Pair;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class King extends Piece {
    public boolean moved = false;

    public King(Board board, int color) {
        super(PieceType.King, board, color);
    }

    @Override
    public void move(Pair to) {
        if (!moved) {
            switch (to.file) {
                case 2:
                    board.getSquare(new Pair(0, to.rank)).piece.move(new Pair(3, to.rank));
                    break;
                case 6:
                    board.getSquare(new Pair(7, to.rank)).piece.move(new Pair(5, to.rank));
                    break;
            }
        }
        moved = true;
        super.move(to);
    }

    @Override
    public void trace(MovesTracer tracer) {
        tracer.go(0, 1);
        tracer.go(1, 1);
        tracer.go(1, 0);
        tracer.go(1, -1);
        tracer.go(0, -1);
        tracer.go(-1, -1);
        tracer.go(-1, 0);
        tracer.go(-1, 1);

        Pair pair = tracer.start;
        if (!moved && pair.rank == (7 - color * 7) / 2 && pair.file == 4) {
//            if (square.captures(this)) {
//                return;
//            }
            if (checkCastlingRule(pair, 1)) {
                Piece rook = board.getSquare(pair.go(3, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(2, 0);
                }
            }
            if (checkCastlingRule(pair, -1)) {
                Piece rook = board.getSquare(pair.go(-4, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(-2, 0);
                }
            }
        }
    }

    private boolean checkCastlingRule(Pair pair, int file) {
        // TODO I don't recalculate pieces, so I must check this rule later!
//        {
//            Square square = board.getSquare(pair.go(file, 0));
//            if (square.piece != null || square.captures(this)) {
//                return false;
//            }
//        }
//        {
//            Square square = board.getSquare(pair.go(2 * file, 0));
//            if (square.piece != null || square.captures(this)) {
//                return false;
//            }
//        }
        return true;
    }
}
