package board.exchange;

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
    // TODO I can move back and simplify score calculation, it shouldn't be very complicated,
    //  and even if it get I need doing it in a smart way not just playing with variants
    protected LinkedList<Piece> pieces = new LinkedList<>();

    protected final Square square;
    protected final HashMap<Integer, Side> sides = new HashMap<>();
    private final int color;
    private int score;

    public Exchange(Square square, int color) {
        this.square = square;
        this.color = color;
    }

    protected void setScene() {
        sides.put(1, new Side(1));
        sides.put(-1, new Side(-1));

        square.attackers().forEach(p -> sides.get(p.color).pieces.add(p));

        score = square.board.score;
    }

    public Result getResult() {
        setScene();
        Result result = sides.get(color).play();
//        square.scores.saveResult(color, result);

        // TODO how do I do it?
        //  I assign a score to each piece and do the calculations once again
        //  artificially counting for this score

        // TODO now I need to calc score with disappeared piece, but without recursion
        //  so I do need different exchanges after all
        //  one is the simple one, and another is a complex one
        //  but I don't need to store anything until a true result is there, or?
        //  let's just not do anything until it harms or benefits

        // TODO but what to do next?
        //  this is simple exchange, I need a complex one
        //  it will be child of this exchange
        log().debug("Result: " + result);
        return result;
    }

    private int getScore(int color) {
        // TODO I need to provide my own score
        //  let's calculate score of my own
        return score * color;
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


    protected class Side {
        final int color;
        // TODO change it with something with additional score
        //  actually I need a score of being there
        //  I don't need any moves
        //  it will be better if I just make my piece disappear
        //  so I need a special move
        //  not to/from one but one removing a piece
        //  I need calculating cost of a removing piece from every exchange except this one
        //  so I need to calculate cost of removing piece from exchange rather then defence score
        //  for that I need to add more pieces to original?
        //  can I use same very exchange to calculate it?
        //  just create an exchange and get participation score
        //  for that I'll need to modify an exchange

        // TODO I need calculate score of exchange without any given piece
        //  how?
        //  let's extend it and implement logic of skipping a piece
        //  I'll later see what it leads to

        // TODO actually, the simplest way is to make a piece disappear
        //  what difference does it make?

        // TODO I also need an overriden score here
        TreeSet<Piece> pieces;

        Integer bestScore;

        public Side(int color) {
            this.color = color;
            this.pieces = new TreeSet<>(Comparator.<Piece>comparingInt(p -> cost(p) * color).thenComparingInt(Object::hashCode));
        }

        private Result getResult() {
            Result result = new Result(score, color);
            storeResults(result);
            sides.get(-color).storeResults(result);
            return result;
        }

        private void storeResults(Result result) {
            result.sides.put(color, new Result.Side(pieces.size()));
        }

        // TODO remove choosing move altogether?
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
                Piece piece = chooseMove();
                pieces.remove(piece);
                score -= cost(piece);

                log().debug("Moving " + piece + ": " + getScore(color));

                if (getScore(color) <= bestScore) {
                    result = sides.get(-color).getResult();
                } else {
                    result = sides.get(-color).play();
                }
                score += cost(piece);
                pieces.add(piece);

                // TODO and I can just return best result as far as I don't store intermediate ones
                if (result.score * color <= lastScore) {
                    result = getResult();
                }
            }

            return result;
        }
    }

    protected int cost(Piece piece) {
        return piece.cost();
    }

    public Logger log() {
        return square.log();
    }

}
