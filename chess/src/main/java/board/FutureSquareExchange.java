package board;

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
        Attack attack = square.attacks.get(through);
        if (attack != null) {
            waypoints.add(attack);
        }
    }

    @Override
    protected void addWaypoint(Waypoint waypoint) {
        if (waypoint.piece != this.through.piece && // Filter out my own moves
                through.square != waypoint.piece.square) { // Filter our pieces I captured
            super.addWaypoint(waypoint);
        }
    }

    @Override
    protected HashSet<Piece> getBlocks(Waypoint waypoint) {
        HashSet<Piece> blocks = super.getBlocks(waypoint);
        if (waypoint.isBlockedBy(through.square)) {
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
