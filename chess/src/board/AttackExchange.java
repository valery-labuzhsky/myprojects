package board;

import board.pieces.Piece;

import java.util.HashSet;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class AttackExchange extends Exchange {
    private final Attack attack;

    public AttackExchange(Attack attack) {
        super(attack.square);
        this.attack = attack;
    }

    @Override
    protected void gatherWaypoints() {
        super.gatherWaypoints();
        addWaypoint(attack);
    }

    @Override
    protected HashSet<Piece> getBlocks(Waypoint waypoint) {
        HashSet<Piece> blocks = super.getBlocks(waypoint);
        if (attack.isBlocking(waypoint)) {
            blocks.add(attack.piece);
        }
        return blocks;
    }

}
