package board.exchange;

import board.Logged;
import board.Move;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class WaypointExchange extends Exchange {
    private final Piece piece;

    public WaypointExchange(Move move) {
        super(move.to, move.color());
        this.piece = move.piece();
    }

    @Override
    protected void setScene() {
        super.setScene();
        makeTurn(piece);
    }

    @Override
    public int getScore() {
        return -super.getScore();
    }

    @Override
    public Logger log() {
        return Logged.log(piece, square);
    }
}
