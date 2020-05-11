package board.exchange;

import board.Logged;
import board.Square;
import board.Waypoint;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange implements Logged {
    // TODO I must really move the pieces

    protected LinkedList<Piece> pieces = new LinkedList<>();

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
        for (Piece piece : pieces) {
            sides.get(piece.color).add(piece);
        }

        onSquare.set(square.piece);
    }

    public Result getResult() {
        setScene();
        Result result = play();
        result.score = result.score * result.lastPlayer * playing.get();
        log().debug("Result: " + result);
        return result;
    }

    private Result play() {
        try {
            return nextTurn();
        } finally {
            playBack();
        }
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
                addPiece(waypoint.piece);
            }
        }
    }

    protected void addPiece(Piece piece) {
        pieces.add(piece);
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

    private static class Turn {
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

    HashMap<Piece, HashSet<Piece>> blockedBy = new HashMap<>();
    HashMap<Piece, HashSet<Piece>> blocking = new HashMap<>();

    protected HashSet<Piece> getBlocks(Piece piece) {
        return new HashSet<>(piece.getBlocks(square));
    }

    public static class Result {
        public int score;
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

            public String toString() {
                return win + ", " + piecesLeft + " left";
            }
        }

        public String toString() {
            return "" + score + ", last " + lastPlayer + " " + sides;
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

        public Result makeMove() {
            int lastScore = score.get();

            if (bestScore.get() == null || bestScore.get() < lastScore) {
                bestScore.set(lastScore);
            }

            if (pieces.isEmpty()) {
                stack.add(new Turn());
                return getResult();
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
            Integer bs = bestScore.get();
            Result result = new Result(bs == null ? 0 : -bs, -color);
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
            HashSet<Piece> blocked = blocking.get(piece);
            if (blocked != null) {
                for (Piece blockedPiece : blocked) {
                    HashSet<Piece> blocks = blockedBy.get(blockedPiece);
                    blocks.remove(piece);
                    playBack(() -> blocks.add(piece));
                    if (blocks.isEmpty()) {
                        sides.get(blockedPiece.color).free(blockedPiece);
                    }
                }
            }
        }

        private void free(Piece piece) {
            this.pieces.add(piece);
            playBack(() -> pieces.remove(piece));
        }

        public void add(Piece piece) {
            HashSet<Piece> blocks = getBlocks(piece);
            if (blocks.isEmpty()) {
                pieces.add(piece);
            } else {
                blockedBy.put(piece, blocks);
                for (Piece block : blocks) {
                    blocking.computeIfAbsent(block, b -> new HashSet<>()).add(piece);
                }
            }
        }
    }

    public Logger log() {
        return square.log();
    }
}
