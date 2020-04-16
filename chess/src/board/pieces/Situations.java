package board.pieces;

import board.Waypoint;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    public final ArrayList<Situation> situations = new ArrayList<>();
    public final HashSet<Waypoint> solutions = new HashSet<>();
    private final Board board;
    public Situation check;
    public int totalScore;

    public Situations(Board board) {
        this.board = board;
    }

    public void lookAt(Piece piece) {
        for (Waypoint waypoint : piece.square.waypoints) {
            if (waypoint.captures()) {
                Situation situation = new Situation(piece, this.board.color);
                if (piece.type == PieceType.King) {
                    check = situation;
                }
                situations.add(situation);
                break;
            }
        }
    }

    public boolean isCheckmate() {
        return check != null && check.solutions.isEmpty();
    }

    public List<Move> getMoves() {
        List<Move> moves = new ArrayList<>();
        for (Waypoint solution : this.solutions) {
            solution.enrich(moves);
        }
        return moves;
    }

    public void analyse() {
        HashMap<Waypoint, Tempo> wouldTempos = new HashMap<>();
        for (Situation situation : situations) {
            for (Solution solution : situation.solutions) {
                wouldTempos.computeIfAbsent(solution.waypoint, Tempo::new).add(solution);
            }
        }

        HashMap<ArrayList<Situation>, Tempos> sameTempos = new HashMap<>();
        for (Tempo tempo : wouldTempos.values()) {
            Tempos tempos = tempo.tempos;
            if (tempos.situations.size() > 1) {
                sameTempos.computeIfAbsent(tempos.situations, s -> tempos).add(tempo);
            }
        }

        ArrayList<Tempos> tempos = new ArrayList<>(sameTempos.values());
        tempos.sort(Comparator.comparingInt(t -> -t.bestScore));

        this.situations.sort(Comparator.comparingInt(s -> -s.bestScore));

        Tempos tempo = null;
        Iterator<Tempos> temporator = tempos.iterator();
        if (temporator.hasNext()) {
            tempo = temporator.next();
        }

        Iterator<Situation> situator = situations.iterator();
        Situation situation = null;
        if (situator.hasNext()) {
            situation = situator.next();
        }

        // TODO this algorithm is somewhat similar to exchange one, I need somehow made it universal

        HashSet<Situation> unsolved = new HashSet<>(this.situations);
        boolean turn = true;
        int bestScore = 0;
        while (situation != null) {
            int tempoScore = tempo == null ? Integer.MIN_VALUE : tempo.bestScore;
            if (tempoScore > situation.bestScore) {
                if (tempoScore > bestScore) {
                    bestScore = tempoScore;
                    tempo.addSolutions(solutions);
                }
                if (unsolved.containsAll(tempo.situations)) {
                    unsolved.removeAll(tempo.situations);
                    if (turn && tempo.bestScore > 0) {
                        totalScore += tempo.score + tempo.bestScore;
                    } else {
                        totalScore += tempo.score;
                    }
                    turn = !turn;
                    System.out.println(tempo);
                }
                if (temporator.hasNext()) {
                    tempo = temporator.next();
                } else {
                    tempo = null;
                }
            } else {
                if (situation.bestScore >= bestScore) {
                    bestScore = situation.bestScore;
                    situation.addSolutions(solutions);
                }
                if (unsolved.remove(situation)) {
                    if (turn && situation.bestScore > 0) {
                        totalScore += situation.score + situation.bestScore;
                    } else {
                        totalScore += situation.score;
                    }
                    turn = !turn;
                    System.out.println(situation);
                }
                if (situator.hasNext()) {
                    situation = situator.next();
                } else {
                    situation = null;
                }
            }
        }

        System.out.println("Total: " + totalScore);
    }

    private static class Tempos {
        int score;
        int bestScore = Integer.MIN_VALUE;

        ArrayList<Situation> situations = new ArrayList<>();
        ArrayList<Tempo> tempos = new ArrayList<>();

        public void add(Situation situation) {
            score += situation.score;
            situations.add(situation);
        }

        public void add(Tempo tempo) {
            tempos.add(tempo);
            if (bestScore < tempo.score) {
                bestScore = tempo.score;
            }
        }

        public void addSolutions(HashSet<Waypoint> solutions) {
            for (Tempo tempo : tempos) {
                if (tempo.score == bestScore) {
                    solutions.add(tempo.solution);
                }
            }
        }

        public String toString() {
            return "" + score + " x " + (score + bestScore) + ": " + printSituations() + " " + printSolutions();
        }

        private String printSituations() {
            return "{" + situations.stream().map(Object::toString).collect(Collectors.joining(", ")) + "}";
        }

        private String printSolutions() {
            return "{" + tempos.stream().
                    filter(s -> s.score == bestScore).map(Object::toString).
                    collect(Collectors.joining(", ")) + "}";
        }

    }


    private static class Tempo {
        Tempos tempos = new Tempos();
        Waypoint solution;
        int score;

        public Tempo(Waypoint waypoint) {
            solution = waypoint;
        }

        public void add(Solution solution) {
            score += solution.score;
            tempos.add(solution.situation);
        }

        public String toString() {
            return solution.toString();
        }
    }
}
