package board;

import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange {
    protected LinkedList<Waypoint> waypoints = new LinkedList<>();

    protected final Square square;
    private final HashMap<Integer, Side> sides = new HashMap<>();

    public Exchange(Square square, int color) {
        this.square = square;
        playing.set(color);
    }

    private final State<Integer> playing = new State<>();
    private final State<Piece> onSquare = new State<>();
    private final State<Integer> score = new State<>(0);

    protected void setScene() {
        gatherWaypoints();

        sides.put(1, new Side(1));
        sides.put(-1, new Side(-1));
        for (Waypoint waypoint : waypoints) {
            Piece piece = waypoint.piece;
            sides.get(piece.color).add(waypoint);
        }

        onSquare.set(square.piece);
    }

    public int getScore() {
        return getResult().score;
    }

    public Result getResult() {
        setScene();
        Result result = play();
        result.score = result.score * result.lastPlayer * playing.get();
        return result;
    }

    private Result play() {
        if (!hasNextTurn()) {
            return sides.get(playing.get()).getResult();
        } else {
            try {
                return nextTurn();
            } finally {
                playBack();
            }
        }
    }

    private boolean hasNextTurn() {
        return !sides.get(playing.get()).isEmpty();
    }

    protected Result nextTurn() {
        return sides.get(playing.get()).makeMove();
    }

    protected void makeTurn(Piece piece) {
        stack.add(new Turn());

        sides.get(playing.get()).makeTurn(piece);

        Piece onSquare = this.onSquare.set(piece);
        if (onSquare != null) {
            score.set(score.get() + onSquare.type.score);
        }
        log().debug("Moving " + piece + ": " + score.get());
        playing.set(-playing.get());
        score.set(-score.get());
    }

    protected void gatherWaypoints() {
        for (Waypoint waypoint : square.waypoints) {
            if (waypoint.isAttack()) {
                addWaypoint(waypoint);
            }
        }
    }

    protected void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    protected class State<E> {
        E value;

        public State() {
        }

        public State(E value) {
            this.value = value;
        }

        public E get() {
            return value;
        }

        public E set(E value) {
            E oldValue = this.value;
            playBack(() -> this.value = oldValue);
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    private final LinkedList<Turn> stack = new LinkedList<>();

    private void playBack(Runnable runnable) {
        if (!stack.isEmpty()) {
            Turn turn = stack.getLast();
            turn.add(runnable);
        }
    }

    private void playBack() {
        stack.removeLast().run();
    }

    private class Turn {
        private final LinkedList<Runnable> revert = new LinkedList<>();

        public void run() {
            for (Runnable runnable : revert) {
                runnable.run();
            }
        }

        public void add(Runnable runnable) {
            revert.addFirst(runnable);
        }
    }

    HashMap<Waypoint, HashSet<Piece>> blockedBy = new HashMap<>();
    HashMap<Piece, HashSet<Waypoint>> blocking = new HashMap<>();

    protected HashSet<Piece> getBlocks(Waypoint waypoint) {
        return new HashSet<>(waypoint.getBlocks());
    }

    public static class Result {
        int score;
        int lastPlayer;

        HashMap<Integer, Side> sides = new HashMap<>();

        public Result(int score, int lastPlayer) {
            this.score = score;
            this.lastPlayer = lastPlayer;
        }

        public static class Side {
            int win;
            int piecesLeft;

            public Side(int win, int piecesLeft) {
                this.win = win;
                this.piecesLeft = piecesLeft;
            }
        }
    }


    private class Side {
        final int color;
        TreeSet<Piece> pieces = new TreeSet<>(Comparator.<Piece>comparingInt(p -> p.type.score).thenComparingInt(Object::hashCode));
        State<Integer> bestScore = new State<>();
        State<Integer> win = new State<>(0);

        public Side(int color) {
            this.color = color;
        }

        private void add(Piece piece) {
            this.pieces.add(piece);
            playBack(() -> pieces.remove(piece));
        }

        public boolean isEmpty() {
            return pieces.isEmpty();
        }

        public Result makeMove() {
            int lastScore = score.get();

            if (bestScore.get() == null || bestScore.get() < lastScore) {
                bestScore.set(lastScore);
            }

            // TODO account for blocks
            Piece piece = chooseMove();
            Exchange.this.makeTurn(piece);

            if (-score.get() < bestScore.get()) {
                playBack();
                stack.add(new Turn());

                return getResult();
            }

            int scoreToZero = bestScore.get() + score.get() + piece.type.score;

            if (scoreToZero <= 0) {
                win.set(piece.type.score);
            } else {
                win.set(piece.type.score - scoreToZero);
            }

            return play();
        }

        private Result getResult() {
            Result result = new Result(-bestScore.get(), -color);
            storeResults(result);
            sides.get(-color).storeResults(result);
            return result;
        }

        private void storeResults(Result result) {
            result.sides.put(color, new Result.Side(win.get(), pieces.size()));
        }

        private Piece chooseMove() {
            return this.pieces.first();
        }

        private void makeTurn(Piece piece) {
            pieces.remove(piece);
            playBack(() -> pieces.add(piece));
            HashSet<Waypoint> blocked = blocking.get(piece);
            if (blocked != null) {
                for (Waypoint waypoint : blocked) {
                    HashSet<Piece> blocks = blockedBy.get(waypoint);
                    blocks.remove(piece);
                    playBack(() -> blocks.add(piece));
                    if (blocks.isEmpty()) {
                        sides.get(waypoint.piece.color).add(waypoint.piece);
                    }
                }
            }
        }

        public void add(Waypoint waypoint) {
            HashSet<Piece> blocks = getBlocks(waypoint);
            if (blocks.isEmpty()) {
                pieces.add(waypoint.piece);
            } else {
                blockedBy.put(waypoint, blocks);
                for (Piece block : blocks) {
                    blocking.computeIfAbsent(block, b -> new HashSet<>()).add(waypoint);
                }
            }
        }
    }

    protected Logger log() {
        return square.log();
    }
}
