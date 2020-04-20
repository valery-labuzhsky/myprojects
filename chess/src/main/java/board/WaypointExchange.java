package board;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class WaypointExchange extends Exchange {
    private final Waypoint waypoint;

    public WaypointExchange(Waypoint waypoint) {
        super(waypoint.square, waypoint.piece.color);
        this.waypoint = waypoint;
    }

    @Override
    protected void setScene() {
        super.setScene();
        makeTurn(waypoint.piece);
    }

    @Override
    public int getScore() {
        return -super.getScore();
    }
}
