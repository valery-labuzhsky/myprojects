package board;

import board.pieces.Piece;

import java.util.*;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange {

    protected LinkedList<Waypoint> waypoints = new LinkedList<>();

    private final Square square;
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

        for (Waypoint waypoint : waypoints) {
            Piece piece = waypoint.piece;
            sides.computeIfAbsent(piece.color, c -> new Side()).add(waypoint);
        }

        onSquare.set(square.piece);
    }

    public int getScore() {
        setScene();
        return play() + score.get();
    }

    private int play() {
        if (!hasNextTurn()) {
            return 0;
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

    protected int nextTurn() {
        int lastScore = score.get();

        makeTurn();

        int score = -play();
        if (score < lastScore) { // TODO make it a choice to make
            return lastScore;
        }
        return 0;
    }

    private void makeTurn() {
        makeTurn(sides.get(playing.get()).chooseMove());
    }

    protected void makeTurn(Piece piece) {
        stack.add(new Turn());

        sides.get(playing.get()).makeTurn(piece);

        Piece onSquare = this.onSquare.set(piece);
        if (onSquare != null) {
            score.set(score.get() + onSquare.type.score);
        }
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
    }

    private final LinkedList<Turn> stack = new LinkedList<>();

    private void playBack(Runnable runnable) {
        Turn turn = stack.getLast();
        if (turn != null) {
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

    private class Side {
        TreeSet<Piece> pieces = new TreeSet<>(Comparator.<Piece>comparingInt(p -> p.type.score).thenComparingInt(Object::hashCode));

        private void add(Piece piece) {
            this.pieces.add(piece);
            playBack(() -> pieces.remove(piece));
        }

        public boolean isEmpty() {
            return pieces.isEmpty();
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

}
