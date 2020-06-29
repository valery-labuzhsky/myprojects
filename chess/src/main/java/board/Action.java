package board;

import board.pieces.Piece;

/**
 * Created on 11.06.2020.
 *
 * @author unicorn
 */
public abstract class Action implements Logged {
    public final Piece piece;

    protected Action(Piece piece) {
        this.piece = piece;
    }

    protected abstract Board board();

    public void imagine() {
        makeMove();
        board().history.push(this, false);
    }

    protected abstract void makeMove();

    public void undo() {
        board().history.pop().undoMove();
    }

    protected abstract void undoMove();
}
