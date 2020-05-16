package board.exchange;

import board.Logged;
import board.Move;
import board.Square;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange implements Logged {
    // TODO current plan
    //  1. Create universal exchange score
    //  2. I must know which moves are good which ar bad just to know my situation here to be able to see what's going on to be able to do rationally
    //  3. So I need to print out bad moves and try to filter them out
    //  4. On the other hand I can create an heuristic to play around attack + defence + freedom and see how one translates to another

    // TODO to create universal exchange score I need:
    //  1. make real moves
    //  2. collect pieces I can move on the fly
    //  3. create universal algorithm for checking variants (branch and bound method)
    //   for that I need a) heuristic b) scoring
    //  it probably worth starting with 3
    //  4. use board score

    // TODO I don't need universal exchange, I need to model all links between exchanges to understand what's going on
    //  to the pin problem: I need to know that moving that piece would cost me additional points as the piece is pinned
    //  I have an exchange and all the actors in it
    //  I need to rise it's role, maybe cache it, that's what I need to know

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

        sides.put(1, new Side(1));
        sides.put(-1, new Side(-1));

        square.attackers().forEach(p -> sides.get(p.color).pieces.add(p));

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

            // TODO try variants
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
            // TODO I should probably add batteries back, but not so stupidly

            // TODO now we know that some piece is pinned
            //  what does it mean to know it?
            //  what effect does it give?
            //  merely more score? it definitely helps
            //  how can I find it out
            //  I must have exchanges for all pieces
            //  to check what it does to an exchange
            //  and I don't always need to play an exchange through to find out that it does nothing
            //  basically every piece is part of other pieces
            //  what about cyclic dependencies?
            //  I don't really have them as my situation is different all the time
            //  was it right decision to do a real moves at all?
            //  I will certainly need it in some future...

            // TODO I need to know that this piece is part of another exchange
            //  see what effect it does to this exchange
            //  isn't it to broad?
            //  let it be that way for a time being

            // TODO now I need to know if this part is participating in another exchange
            //  so I need to include all that pieces in my arsenal
            //  just to be able to see what effect does it do

            // TODO here I may introduce roles

            // TODO how do I do it
            //  Exchange is not yet calculated
            //  So the first thing is to store exchanges somewhere
            //  It must be emptied after every move (for now at least)
            //  then I need to calculate it
            //  I must calculate additional cost once and recalculate it on some event

            // TODO so I need:
            //  1. make Exchange recursive
            //  2. take board score
            //  3. cache exchange and change it only on situation is changed (it is useful not only for performance but for monitoring all that cases as well)
            return this.pieces.first();
        }

        private void makeTurn(Piece piece) {
            Move move = piece.move(square);
            move.imagine();
            playBack(move::undo);

            pieces.remove(piece);
            playBack(() -> pieces.add(piece));
        }

    }

    public Logger log() {
        return square.log();
    }
}
