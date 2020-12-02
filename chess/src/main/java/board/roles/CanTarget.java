package board.roles;

import board.Square;
import board.pieces.Piece;

/**
 * Created on 01.12.2020.
 *
 * @author unicorn
 */
public abstract class CanTarget extends Role {
    public final Piece whom; // TODO combine with target
    protected final Square through;

    public CanTarget(Piece piece, Piece whom, Square through) {
        super(piece);
        this.whom = whom;
        this.through = through;
    }

    @Override
    public Piece getWhom() {
        return whom;
    }

    @Override
    public int getScore() {
        return 0;
    }

    public String toString() {
        return piece + " can " + verb() + " " + whom + " through " + through;
    }

    protected abstract String verb();
}
