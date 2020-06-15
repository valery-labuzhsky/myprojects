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
    private Piece onSquare;


    public Exchange(Square square, int color) {
        this.square = square;
        this.color = color;
    }

    protected void setScene() {
        sides.put(1, new Side(1));
        sides.put(-1, new Side(-1));

        square.attackers().forEach(this::addPiece);

//        score = square.board.score;
        onSquare = square.piece;
    }

    protected boolean addPiece(Piece p) {
        return sides.get(p.color).pieces.add(p);
    }

    public Result getResult() {
        return stack(() -> {
            setScene();
            log().debug("" + onSquare + ": " + sides.get(1).pieces + " vs " + sides.get(-1).pieces);
            Result result = sides.get(color).play();
//        square.scores.saveResult(color, result);

            log().debug("Result: " + result);
            return result;
        });
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
        Result bestResult;

        public Side(int color) {
            this.color = color;
            this.pieces = new TreeSet<>(Comparator.<Piece>comparingInt(p -> (cost(p) + p.cost()) * color).thenComparingInt(Object::hashCode));
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
                bestResult = getResult();
            }

            Result result;

            if (pieces.isEmpty()) {
//                result = getResult();
                result = bestResult;
            } else {
                Piece piece = chooseMove();
                pieces.remove(piece);
                score += cost(piece) - onSquare.cost();
                onSquare = piece;

                log().debug("Moving " + piece + ": " + score);

                if (getScore(color) <= bestScore) {
//                    result = sides.get(-color).getResult();
                    result = bestResult;
                } else {
                    result = sides.get(-color).play();
                }
//                score += cost(piece);
//                pieces.add(piece);

                // TODO and I can just return best result as far as I don't store intermediate ones
                // TODO it depends on everything put back
//                if (result.score * color <= lastScore) {
//                    result = getResult();
//                }
            }

            return result;
        }
    }

    protected int cost(Piece piece) {
        return 0;
    }

    public Logger getLogger() {
        return square.getLogger();
    }

}
