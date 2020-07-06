package board.situation;

import java.util.ArrayList;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public abstract class Problem {
    final ArrayList<Solution> solutions = new ArrayList<>();

    public abstract int getScore();
}
