package board.pieces;

import board.MovesTracer;
import board.Square;
import board.Waypoint;

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
    public void trace(MovesTracer tracer) {
        tracer.start();
        tracer.step(0, color);
        // enpassant
        int border = (7 - color * 7) / 2;
        int secondRow = border + color;
        if (square.pair.rank == secondRow) {
            tracer.step(0, color);
        }
        tracer.end();

        tracer.go(-1, color);
        tracer.go(1, color);
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        Square square = waypoint.getOriginalSquare();
        return waypoint.square.pair.file == square.pair.file;
    }

    @Override
    public boolean attacks(Waypoint waypoint) {
        Square square = waypoint.getOriginalSquare();
        return waypoint.square.pair.file != square.pair.file;
    }

}
