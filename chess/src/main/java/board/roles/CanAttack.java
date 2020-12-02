package board.roles;

import board.Square;
import board.pieces.Piece;

/**
 * Created on 01.12.2020.
 *
 * @author unicorn
 */
public class CanAttack extends CanTarget {

    public CanAttack(Piece piece, Piece whom, Square through) {
        super(piece, whom, through);
    }

    @Override
    protected String verb() {
        return "attack";
    }

}
