package board.situation;

import board.Board;
import board.Logged;
import board.Move;
import board.Waypoint;
import board.exchange.ComplexExchange;
import board.pieces.Piece;
import board.pieces.PieceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    private final HashSet<Move> moves = new HashSet<>();

    public final Board board;
    private Situation check;
    public int score;
    private int defenceScore;

    public Situations(Board board) {
        this.board = board;
        analyse();
    }

    private static <S, C extends Comparable<C>, L extends Collection<S>> L best(Collection<S> input, L output, Function<S, C> score) {
        best(input.iterator(), score, output::add, output::clear);
        return output;
    }

    private static <C, L extends Collection<C>> L best(Collection<C> input, L output, Function<C, Integer> score, int color) {
        return best(input, output, colored(score, color));
    }

    private static <S, C extends Comparable<C>> ArrayList<S> best(Collection<S> input, Function<S, C> score) {
        return best(input, new ArrayList<>(), score);
    }

    static <C> ArrayList<C> best(Collection<C> input, Function<C, Integer> score, int color) {
        return best(input, colored(score, color));
    }

    private static <C> Function<C, Integer> colored(Function<C, Integer> simple, int color) {
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

    public int result() {
        return this.score + defenceScore;
    }

    public boolean isCheckmate() {
        return check != null && result() < -PieceType.King.score / 2;
    }

    private void analyse() {
        ArrayList<AttackSituation> attacks = new ArrayList<>();
        HashSet<DefenceScore> solutions = new HashSet<>();
        for (Piece piece : new ArrayList<>(board.pieces.get(-board.color))) { // TODO because we are doing moves when analysing situation
            // TODO I can optimize it by getting rid of waypoints
            for (Waypoint waypoint : piece.square.waypoints) {
                if (waypoint.captures()) {
                    // TODO I only count for exchanges right now
                    //  I can check for situation he cannot escape
                    //  they are equal with exchanges and must be compared together
                    AttackSituation situation = new AttackSituation(piece);
                    attacks.add(situation);

                    solutions.addAll(situation.defences); // TODO they are not defences

                    break;
                }
            }
        }

        log().info(Logged.tabs("Attacks", attacks));

        ArrayList<DefenceSituation> defences = new ArrayList<>();
        for (Piece piece : new ArrayList<>(board.pieces.get(board.color))) { // TODO because we are doing moves when analysing situation
            // TODO I can optimize it by getting rid of waypoints
            for (Waypoint waypoint : piece.square.waypoints) {
                if (waypoint.captures()) {
                    DefenceSituation situation = new DefenceSituation(piece);
                    defences.add(situation);
                    solutions.addAll(situation.defences);

                    if (piece.type == PieceType.King) {
                        check = situation;
                    }
                    score += situation.score();
                    break;
                }
            }
        }

        log().info(Logged.tabs("Defences", defences));

        solutions.removeIf(s -> s.getScore() * board.color < 0);

        if (solutions.isEmpty()) {
            for (Piece piece : new ArrayList<>(board.pieces.get(board.color))) { // TODO because we are doing moves when analysing situation
                for (Waypoint move : piece.getMoves()) {
                    solutions.add(new DefenceScore(move.move()));
                }
            }
            solutions = best(solutions, new HashSet<>(), d -> d.getScore(), board.color);
            log().info(Logged.tabs("Best moves", solutions));
        } else {
            solutions = best(solutions, new HashSet<>(), d -> d.getScore(), board.color);
        }

        List<RetaliationScore> retaliationScores = solutions.stream().map(a -> new RetaliationScore(a.move)).collect(Collectors.toList());
        log().info(Logged.tabs("Retaliation scores", retaliationScores));
        retaliationScores = best(retaliationScores, a -> a.getScore(), board.color);

        List<AttackScore> attackScores = retaliationScores.stream().map(a -> new AttackScore(a.myMove, ComplexExchange::diff)).collect(Collectors.toList());
        log().info(Logged.tabs("Attack scores", attackScores));
        attackScores = best(attackScores, a -> a.getScore(), board.color);
        // TODO print them
        //  something is wrong
        //  I have list of moves and I'd like to apply sequential filters
        //  but I also need to print this criteria
        //  that's why the list must consist of new collection all the time
        //  I can unify how I get move out of it and how I get score out of it

        // TODO I need transforming them from one to another
        //  do I ever need it or kludge will do for a while?
        //  I may rewrite it many times


        // TODO then I need collection for attacks to print it
        //  why do I even need it?
        attackScores.forEach(m -> moves.add(m.move));

        solutions.stream().findFirst().ifPresent(m -> defenceScore = m.getScore());

        System.out.println("Total: " + score);
        System.out.println("Defend: " + defenceScore + " " + Logged.tabs(this.moves));
//
//        for (DefenceScore move : moves) {
//            log().info("Retaliation " + new RetaliationScore(move.move));
//        }
    }

    private Logger log() {
        return LogManager.getLogger("situations");
    }

    private Move chooseMove() {
        Move badMove = board.parse("a5a6");
        if (moves.contains(badMove)) {
            moves.clear();
            moves.add(badMove);
        }

        HashMap<Piece, ArrayList<Move>> allMoves = new HashMap<>();
        for (Move move : moves) {
            allMoves.computeIfAbsent(move.piece, p -> new ArrayList<>()).add(move);
        }

        if (!allMoves.isEmpty()) {
            Piece piece = allMoves.keySet().stream().skip(board.random.nextInt(allMoves.size())).findFirst().orElse(null);
            ArrayList<Move> moves = allMoves.get(piece);
            return moves.get(board.random.nextInt(moves.size()));
        }

        return null;
    }

    public Move getMove() {
        return chooseMove();
    }

}
