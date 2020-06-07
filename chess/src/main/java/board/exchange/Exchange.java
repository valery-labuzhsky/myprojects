package board.exchange;

import board.Board;
import board.Logged;
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
    protected LinkedList<Piece> pieces = new LinkedList<>();

    protected final Square square;
    private final HashMap<Integer, Side> sides = new HashMap<>();
    private final int color;

    public Exchange(Square square, int color) {
        this.square = square;
        this.color = color;
    }

    protected void setScene() {

        sides.put(1, new Side(1));
        sides.put(-1, new Side(-1));

        square.attackers().forEach(p -> sides.get(p.color).pieces.add(p));
    }

    public Result getResult() {
        setScene();
        sides.get(color).play();

        // TODO now I need to calculate each piece move cost
        //  actually, I should count for this square count
        //  the score of this square implies that the move was actually done
        //  I must calculate the score of all the others exchanges except this very exchange
        //  how to use this score?
        //  after the move of my piece I should do all the other moves
        //  let's do it simple:
        //  each piece has a score
        //  I add this score to the resulting score
        //  impreciseness is a good thing
        //  it differentiate main law against additional ones
        Result result = square.getExchangeResult(color);
        log().debug("Result: " + result);
        return result;
    }

    public int getScore(int color) {
        return board().score(color);
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
            final int piecesLeft;

            public Side(int piecesLeft) {
                this.piecesLeft = piecesLeft;
            }

            public String toString() {
                return piecesLeft + " left";
            }
        }

        public String toString() {
            return "" + score + ", last " + lastPlayer + " " + sides;
        }
    }


    private class Side {
        final int color;
        TreeSet<Piece> pieces = new TreeSet<>(Comparator.<Piece>comparingInt(p -> p.type.score).thenComparingInt(Object::hashCode));

        Integer bestScore;

        public Side(int color) {
            this.color = color;
        }

        private Result getResult() {
            Result result = new Result(board().score, color);
            storeResults(result);
            sides.get(-color).storeResults(result);
            return result;
        }

        private void storeResults(Result result) {
            result.sides.put(color, new Result.Side(pieces.size()));
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
            //  -1. make Exchange recursive
            //  -2. take board score
            //  3. cache exchange and change it only on situation is changed (it is useful not only for performance but for monitoring all that cases as well)
            return this.pieces.first();
        }

        private Result play() {
            int lastScore = getScore(color);

            if (bestScore == null || bestScore < lastScore) {
                bestScore = lastScore;
            }

            Result result;

            if (pieces.isEmpty()) {
                result = getResult();
            } else {
                // TODO try variants
                Piece piece = chooseMove();
                piece.move(square).imagine();
                pieces.remove(piece);

                log().debug("Moving " + piece + ": " + getScore(color));

                if (getScore(color) <= bestScore) {
                    result = sides.get(-color).getResult();
                } else {
                    result = sides.get(-color).play();
                }
                pieces.add(piece);
                board().undo();

                board();
                if (result.score * color <= lastScore) {
                    result = getResult();
                }
            }

            square.scores.saveResult(color, result);
            return result;
        }
    }

    public Board board() {
        return square.board;
    }

    public Logger log() {
        return square.log();
    }
}
