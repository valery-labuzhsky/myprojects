package board.roles;

import board.Square;
import board.pieces.Piece;

/**
 * Created on 01.12.2020.
 *
 * @author unicorn
 */
public class CanProtect extends CanTarget {
    public CanProtect(Piece piece, Piece whom, Square through) {
        super(piece, whom, through);
    }

    @Override
    protected String verb() {
        return "protect";
    }
}
