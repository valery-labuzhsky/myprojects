package board.situation;

import board.Board;
import board.Logged;
import board.Move;
import board.exchange.ComplexExchange;
import board.pieces.Piece;
import board.pieces.PieceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static board.Logged.tabs;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    private final HashSet<Move> moves = new HashSet<>();

    public final Board board;
    private Problem check;
    public int score;

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
        return this.score - 1000;
    }

    public boolean isCheckmate() {
        return check != null && result() < -PieceType.King.score / 2;
    }

    private void analyse() {
        // TODO what information I need next?
        //  display his moves which I need react to
        //  and mine too
        // TODO let's reduce noise
        log("My roles", board.friends().stream().flatMap(p -> p.meaningfulRoles()));
        log("His roles", board.enemies().stream().flatMap(p -> p.meaningfulRoles()));

        ArrayList<AfterMoveScore> myAttacks = new ArrayList<>();
        ArrayList<Solution> captures = new ArrayList<>();

        for (Piece piece : board.enemies()) {
            // TODO they are my threat roles
            captures.addAll(new CaptureTroubleMaker(piece).takeAdvantageOf().collect(Collectors.toList()));

            // TODO it is not necessary simple, it may be complex
            myAttacks.addAll(new SimpleAttackTroubleMaker(piece).attacks);
        }

        log("My attacks", myAttacks.stream());
        log("My captures", captures.stream().map(s -> s.problem));

        ArrayList<ProblemSolver> oppositeAttacks = new ArrayList<>();

        for (Piece piece : board.friends()) {
            oppositeAttacks.addAll(new CaptureTroubleMaker(piece).makeProblems().collect(Collectors.toList()));
            oppositeAttacks.addAll(new OppositeAttacksNoEscapeTroubleMaker(piece).makeProblems().collect(Collectors.toList()));
        }

        for (ProblemSolver attack : oppositeAttacks) {
            attack.counterAttacks(myAttacks);
            attack.captures(captures);
        }

        log("Problems", oppositeAttacks.stream());
        score += oppositeAttacks.stream().mapToInt(a -> a.getScore()).sum();

        HashMap<Move, Tempo> tempos = new HashMap<>();
        for (ProblemSolver attack : oppositeAttacks) {
            for (Solution solution : attack.getSolutions()) {
                tempos.compute(solution.move, (m, t) -> t == null ? new Tempo(solution) : t.add(solution));
            }
        }
        for (Solution solution : captures) {
            // TODO duplicate
            tempos.compute(solution.move, (m, t) -> t == null ? new Tempo(solution) : t.add(solution));
        }

        ArrayList<Tempo> bestTempos = best(tempos.values(), t -> t.getScore(), board.color);
        log("Solutions", bestTempos.stream());

        HashSet<Problem> unsolvedProblems = oppositeAttacks.stream().map(a -> a.problem).
                collect(Collectors.toCollection(() -> new HashSet<>()));
        bestTempos.forEach(t -> unsolvedProblems.removeAll(t.solves));

        log("Unsolved problems", Logged.shortTabs("", unsolvedProblems));

        check = unsolvedProblems.stream().
                filter(p -> p.piece.type == PieceType.King).findAny().orElse(null);

        HashSet<SamePiecesMoveScore> solutions = bestTempos.stream().map(t -> new SamePiecesMoveScore(t.move)).collect(Collectors.toCollection(HashSet::new));
        score += bestTempos.stream().map(t -> t.getScore()).findAny().orElse(0);

        if (solutions.isEmpty()) {
            for (Piece piece : board.friends()) {
                piece.whereToMove().forEach(move -> solutions.add(new SamePiecesMoveScore(piece.move(move))));
            }
        }

        HashSet<SamePiecesMoveScore> bestMoves = best(solutions, new HashSet<>(), d -> d.getScore(), board.color);
        log("Best moves", bestMoves.stream());

        List<RetaliationScore> retaliationScores = bestMoves.stream().map(a -> new RetaliationScore(a.move)).collect(Collectors.toList());
        log("Retaliation scores", retaliationScores.stream());
        retaliationScores = best(retaliationScores, a -> a.getScore(), board.color);

        List<OppositePiecesDiffMoveScore> oppositeScores = retaliationScores.stream().map(a -> new OppositePiecesDiffMoveScore(a.myMove, ComplexExchange::diff)).collect(Collectors.toList());
        log("Opposite scores", ((Collection<?>) oppositeScores).stream());
        oppositeScores = best(oppositeScores, a -> a.getScore(), board.color);
        oppositeScores.forEach(m -> moves.add(m.move));

        // TODO I need to know:
        //  how can he attack me so that I need to take actions
        //  most important, how I can attack him so he need to take actions
        //  next thing I need to know is how can I improve my score
        //  I may have 8 different goals
        //  sum x max x his/mine
        //  but first I need to display them
        //  opposite scores are not relevant, even harmful,
        //  but I'll remove them once I'm done with this project

        // TODO see all potential threats mine and his
        // TODO display locked pieces - e3 is protecting f4 from d6
        System.out.println("Score: " + score + " " + this.moves);
    }

    private void log(String name, Stream<?> list) {
        log(name, tabs(list));
    }

    private void log(String name, String tabs) {
        if (!tabs.isEmpty()) log().info(name + tabs);
    }

    private Logger log() {
        return LogManager.getLogger("situations");
    }

    private Move chooseMove() {
        Move badMove = board.parse("f4e5");
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
