package board;

/**
 * Created on 19.04.2020.
 *
 * @author ptasha
 */
public abstract class MovesTracer {
    public Pair start;
    public Pair pair;

    public MovesTracer(Pair pair) { // TODO squares will be more convenient?
        this.start = pair;
    }

    public void markLine(int file, int rank) {
        start();
        do {
            if (!step(file, rank)) {
                break;
            }
        } while (pair != null);
        end();
    }

    public void start() {
        pair = start;
    }

    public void end() {
    }

    public void go(int file, int rank) {
        start();
        step(file, rank);
        end();
    }

    public boolean step(int file, int rank) {
        pair = pair.go(file, rank);
        if (!pair.isValid()) {
            pair = null;
        }
        if (pair != null) {
            return step();
        }
        return true;
    }

    protected abstract boolean step();
}
