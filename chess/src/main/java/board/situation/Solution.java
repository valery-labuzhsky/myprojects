package board.situation;

import board.Move;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Solution {
    final Move move;
    Problem problem;

    Solution(Move move, Problem problem) {
        this.move = move;
        this.problem = problem;
    }

    @Override
    public String toString() {
        return move.toString();
    }

    int getNegative() {
        return 0;
    }
}
