package board.situation;

import board.Move;
import board.Square;
import board.exchange.Exchange;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Solution {
    final Move move;
    Problem problem;
    private final String name;

    @Deprecated
    Solution(Move move, Problem problem) {
        this("You name it", move, problem);
    }

    Solution(String name, Move move, Problem problem) {
        this.move = move;
        this.problem = problem;
        this.name = name;
    }

    public int whyNots() {
        // TODO simple int isn't good enough
        return trap();
    }

    private int trap() {
        int trap = 0;
        Square to = move.to;
        if (to.piece == null) {
            Exchange afterMove = to.scores.getExchange(move.piece.color).move(move.piece);
            trap = afterMove.getScore() * move.piece.color;
            // TODO I displayed it well so what then?
            //  I must somehow incorporate it into decision making system
            //  it doesn't really belong here, but I need to display it somewhere here
            //  I may calculate it after, or I may have means of calculating it
            //  actually I didn't make it part of tempos yet
            //  so I must check it somewhere else
            //  it's not the problem of it's own, because it doesn't have move
        } else {
            // TODO what then?
        }
        return trap;
    }

    @Override
    public String toString() {
        int trap = trap();
        return move.piece + " " + name + " to " + move.to + (trap < 0 ? " trap " + trap : "");
    }
}
