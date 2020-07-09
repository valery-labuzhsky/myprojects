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
    private Variants check;
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
        ArrayList<AfterMoveScore> myAttacks = new ArrayList<>();
        ArrayList<Solution> captures = new ArrayList<>();

        // TODO I'm capturing 900
        //  that's what I should care about
        //  with this move I should jump into solution
        //  just add a solution to no problem in particular
        //  why not
        for (Piece piece : new ArrayList<>(board.pieces.get(-board.color))) { // TODO because we are doing moves when analysing situation
            captures.addAll(new CaptureTroubleMaker(piece).takeAdvantageOf().collect(Collectors.toList()));

            // TODO it is not necessary simple, it may be complex
            myAttacks.addAll(new SimpleAttackTroubleMaker(piece).attacks);
        }

        log().info(Logged.tabs("My attacks", myAttacks));

        log().info(Logged.tabs("My captures", captures));

        ArrayList<ProblemSolver> oppositeAttacks = new ArrayList<>();

        for (Piece piece : new ArrayList<>(board.pieces.get(board.color))) { // TODO because we are doing moves when analysing situation
            oppositeAttacks.addAll(new CaptureTroubleMaker(piece).makeProblems(myAttacks).collect(Collectors.toList()));
            oppositeAttacks.addAll(new OppositeAttacksNoEscapeTroubleMaker(piece).makeProblems(myAttacks).collect(Collectors.toList()));
        }

        log().info(Logged.tabs("Problems", oppositeAttacks));
        score += oppositeAttacks.stream().mapToInt(a -> a.getScore()).sum();

        HashMap<Move, Tempo> tempos = new HashMap<>();
        for (ProblemSolver attack : oppositeAttacks) {
            for (Solution solution : attack.getSolutions()) {
                tempos.compute(solution.move, (m, t) -> t == null ? new Tempo(solution) : t.add(solution));
            }
        }
        ArrayList<Tempo> bestTempos = best(tempos.values(), t -> t.getScore(), board.color);
        log().info(Logged.tabs("Solutions", bestTempos));

        HashSet<ProblemSolver> unsolvedProblems = new HashSet<>(oppositeAttacks);
        bestTempos.forEach(t -> unsolvedProblems.removeAll(t.problems));

        // TODO save worst unsolved problems
        //  it will be our decision maker
        log().info(Logged.shortTabs("Unsolved problems", unsolvedProblems));
        check = unsolvedProblems.stream().
                filter(p -> p instanceof CaptureProblemSolver).map(p -> (CaptureProblemSolver) p).
                filter(p -> p.piece.type == PieceType.King).findAny().orElse(null);

        HashSet<SamePiecesMoveScore> solutions = new HashSet<>(bestTempos.stream().map(t -> new SamePiecesMoveScore(t.move)).collect(Collectors.toList()));
        score += bestTempos.stream().map(t -> t.getScore()).findAny().orElse(0);

        solutions.removeIf(s -> s.getScore() * board.color < 0);

        if (solutions.isEmpty()) {
            for (Piece piece : new ArrayList<>(board.pieces.get(board.color))) { // TODO because we are doing moves when analysing situation
                for (Waypoint move : piece.getMoves()) {
                    solutions.add(new SamePiecesMoveScore(move.move()));
                }
            }
        }

        solutions = best(solutions, new HashSet<>(), d -> d.getScore(), board.color);
        log().info(Logged.tabs("Best moves", solutions));

        List<RetaliationScore> retaliationScores = solutions.stream().map(a -> new RetaliationScore(a.move)).collect(Collectors.toList());
        log().info(Logged.tabs("Retaliation scores", retaliationScores));
        retaliationScores = best(retaliationScores, a -> a.getScore(), board.color);

        List<OppositePiecesDiffMoveScore> oppositeScores = retaliationScores.stream().map(a -> new OppositePiecesDiffMoveScore(a.myMove, ComplexExchange::diff)).collect(Collectors.toList());
        log().info(Logged.tabs("Opposite scores", oppositeScores));
        oppositeScores = best(oppositeScores, a -> a.getScore(), board.color);
        oppositeScores.forEach(m -> moves.add(m.move));

        solutions.stream().findFirst().ifPresent(m -> defenceScore = m.getScore());

        System.out.println("Total: " + score);
        System.out.println("Defend: " + defenceScore + " " + Logged.tabs(this.moves));
    }

    private Logger log() {
        return LogManager.getLogger("situations");
    }

    private Move chooseMove() {
        Move badMove = board.parse("e1f2");
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
