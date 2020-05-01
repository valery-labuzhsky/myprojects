package board.exchange;

import board.Square;
import board.Waypoint;
import board.pieces.Piece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

/**
 * Created on 21.04.2020.
 *
 * @author ptasha
 */
public class FutureSquareExchange extends Exchange {
    private final Waypoint through;

    public FutureSquareExchange(Square square, int color, Waypoint through) {
        super(square, color);
        this.through = through;
    }

    @Override
    protected void gatherWaypoints() {
        super.gatherWaypoints();
        if (through.piece.isAttack(through.square, square)) {
            pieces.add(through.piece);
        }
    }

    @Override
    protected void addPiece(Piece piece) {
        if (piece != this.through.piece && // Filter out my own moves
                through.square != piece.square) { // Filter our pieces I captured
            super.addPiece(piece);
        }
    }

    @Override
    protected HashSet<Piece> getBlocks(Piece piece) {
        HashSet<Piece> blocks = super.getBlocks(piece);
        if (piece.ray(square).anyMatch(s -> through.square == s)) {
            blocks.add(this.through.piece);
        } else {
            blocks.remove(this.through.piece);
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
        return LogManager.getLogger(through.log().getName() + "." + super.square.log().getName());
    }

}
