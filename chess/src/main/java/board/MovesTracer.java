package board;

import board.pieces.Board;

/**
 * Created on 19.04.2020.
 *
 * @author ptasha
 */
public abstract class MovesTracer {
    private final Board board;
    public Square start;
    public Square now;

    public MovesTracer(Board board, Square start) {
        this.board = board;
        this.start = start;
    }

    public void markLine(int file, int rank) {
        start();
        do {
            if (!step(file, rank)) {
                break;
            }
        } while (now != null);
        end();
    }

    public void start() {
        now = start;
    }

    public void end() {
    }

    public void go(int file, int rank) {
        start();
        step(file, rank);
        end();
    }

    public boolean step(int file, int rank) {
        Pair go = now.pair.go(file, rank);
        if (!go.isValid()) {
            now = null;
        } else {
            now = board.getSquare(go);
        }
        if (now != null) {
            return step();
        }
        return true;
    }

    protected abstract boolean step();
}
