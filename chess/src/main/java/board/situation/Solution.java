package board.situation;

import board.Logged;
import board.Move;
import board.Waypoint;
import board.exchange.ComplexExchange;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Solution implements Logged {
    public final Move move;
    private DefenceCalculator defenceWatcher;
    private AttackCalculator attackWatcher;

    public Solution(Waypoint way) {
        this(way.move());
    }

    public Solution(Move move) {
        this.move = move;
        // TODO I cannot calculate it lazily - move might be illegal
        calculateDefence();
        calculateAttack();
    }

    public static ArrayList<Solution> best(Collection<Solution> input, int color) {
        return best(best(input, Solution::getDefence, color), Solution::getAttack, color);
    }

    private static <S, C extends Comparable<C>> ArrayList<S> best(Collection<S> input, Function<S, C> score) {
        ArrayList<S> filtered = new ArrayList<>();
        best(input.iterator(), score, filtered::add, filtered::clear);
        return filtered;
    }

    public static <C> ArrayList<C> best(Collection<C> input, Function<C, Integer> score, int color) {
        return best(input, colored(score, color));
    }

    public static <C> Function<C, Integer> colored(Function<C, Integer> simple, int color) {
        return c -> color * simple.apply(c);
    }

    private static <S, C extends Comparable<C>> void best(Iterator<S> input, Function<S, C> score, Consumer<S> output, Runnable clear) {
        if (input.hasNext()) {
            S solution;
            solution = input.next();
            C best = score.apply(solution);
            output.accept(solution);
            while (input.hasNext()) {
                solution = input.next();
                C solutionScore = score.apply(solution);
                int diff = best.compareTo(solutionScore);
                if (diff < 0) {
                    best = solutionScore;
                    clear.run();
                    output.accept(solution);
                } else if (diff == 0) {
                    output.accept(solution);
                }
            }
        }
    }

    public String toString() {
        return "+" + getDefenceWatcher() + "\n-" + getAttackWatcher();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Solution solution = (Solution) o;
        return move.equals(solution.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move);
    }

    @Override
    public Logger getLogger() {
        return move.getLogger();
    }

    private void calculateDefence() {
        if (defenceWatcher == null) {
            defenceWatcher = new DefenceCalculator(move);
            defenceWatcher.calculate();
        }
    }

    private DefenceCalculator getDefenceWatcher() {
        calculateDefence();
        return defenceWatcher;
    }

    public int getDefence() {
        return getDefenceWatcher().getScore();
    }

    private void calculateAttack() {
        if (attackWatcher == null) {
            attackWatcher = new AttackCalculator(move, ComplexExchange::diff);
            attackWatcher.calculate();
        }
    }

    private AttackCalculator getAttackWatcher() {
        calculateAttack();
        return attackWatcher;
    }

    public int getAttack() {
        return getAttackWatcher().getScore();
    }
}
