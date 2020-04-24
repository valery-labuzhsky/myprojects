package board.pieces;

import board.MovesTracer;
import board.Pair;
import board.Square;
import board.Waypoint;

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
            moved = true;
        } else {
            int diff = to.file - square.pair.file;
            Rook rook;
            switch (diff) {
                case -2:
                    rook = (Rook) board.getSquare(new Pair(5, to.rank)).piece;
                    rook.move(new Pair(7, to.rank));
                    rook.moved = false;
                    moved = false;
                    break;
                case 2:
                    rook = (Rook) board.getSquare(new Pair(3, to.rank)).piece;
                    rook.move(new Pair(0, to.rank));
                    rook.moved = false;
                    moved = false;
                    break;
            }
        }
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

        Pair pair = tracer.start.pair;
        if (!moved && pair.rank == (7 - color * 7) / 2 && pair.file == 4) {
            {
                Piece rook = board.getSquare(pair.go(3, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(2, 0);
                }
            }
            {
                Piece rook = board.getSquare(pair.go(-4, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(-2, 0);
                }
            }
        }
    }

    @Override
    public boolean attacks(Waypoint waypoint) {
        if (!super.attacks(waypoint)) {
            return false;
        }
        if (!moved) {
            return waypoint.square.pair.file != 2 && waypoint.square.pair.file != 6;
        }
        return true;
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        if (!super.goes(waypoint)) {
            return false;
        }
        if (!moved) {
            if (waypoint.square.pair.file == 2) {
                return !isInDanger() && checkCastlingRule(square.pair, -1);
            } else if (waypoint.square.pair.file == 6) {
                return !isInDanger() && checkCastlingRule(square.pair, 1);
            }
        }
        return true;
    }

    private boolean checkCastlingRule(Pair pair, int file) {
        {
            Square square = board.getSquare(pair.go(file, 0));
            if (square.piece != null || square.captures(this)) {
                return false;
            }
        }
        {
            Square square = board.getSquare(pair.go(2 * file, 0));
            if (square.piece != null || square.captures(this)) {
                return false;
            }
        }
        return true;
    }
}
