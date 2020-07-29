package board.situation;

import board.pieces.Piece;

/**
 * Created on 28.06.2020.
 *
 * @author unicorn
 */
public interface Analytics {
    int getScore();

    default int getScore(Piece side) {
        return getScore() * side.color;
    }
}
