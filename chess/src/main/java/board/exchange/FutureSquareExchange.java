package board.exchange;

import board.Logged;
import board.Move;
import board.Square;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

/**
 * Created on 21.04.2020.
 *
 * @author ptasha
 */
public class FutureSquareExchange extends Exchange implements Logged {
    private final Piece piece;
    private final Square to;

    public FutureSquareExchange(Square square, int color, Move move) {
        super(square, color);
        this.piece = move.piece();
        this.to = move.to;
    }

    @Override
    protected void gatherWaypoints() {
        super.gatherWaypoints();
        if (piece.isAttack(to, square)) {
            pieces.add(piece);
        }
    }

    @Override
    protected void addPiece(Piece piece) {
        if (piece != this.piece && // Filter out my own moves
                to != piece.square) { // Filter our pieces I captured
            super.addPiece(piece);
        }
    }

    @Override
    protected HashSet<Piece> getBlocks(Piece piece) {
        HashSet<Piece> blocks = super.getBlocks(piece);
        if (piece.ray(square).anyMatch(s -> to == s)) {
            blocks.add(this.piece);
        } else {
            blocks.remove(this.piece);
        }
        return blocks;
    }

    @Override
    public int getScore() {
        int score = super.getScore();
        log().debug("Score: " + score);
        return score;
    }

    public Logger log() {
        return Logged.log(piece, to, square);
    }

}
