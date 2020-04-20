package board;

import board.pieces.*;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class PieceBuilder {
    private final Board board;
    private final int color;

    public PieceBuilder(Board board, int color) {
        this.board = board;
        this.color = color;
    }

    public Rook R() {
        return new Rook(board, color);
    }

    public Knight N() {
        return new Knight(board, color);
    }

    public Bishop B() {
        return new Bishop(board, color);
    }

    public Queen Q() {
        return new Queen(board, color);
    }

    public King K() {
        return new King(board, color);
    }

    public Pawn P() {
        return new Pawn(board, color);
    }
}
