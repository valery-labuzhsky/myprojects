package board;

import java.util.*;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Square {
    private final Board board;
    public final Pair pair;
    public Piece piece;

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final HashSet<Attack> attacks = new HashSet<>();

    public Square(Board board, Pair pair) {
        this.board = board;
        this.pair = pair;
    }

    @Override
    public String toString() {
        return toString(this.piece) + lastMove() + marks();
    }

    private String marks() {
        StringBuilder go = new StringBuilder("");
        for (Waypoint waypoint : waypoints) {
            if (waypoint.obstructed.isEmpty()) {
                go.append(toString(waypoint.piece));
            }
        }
        return go.length() > 0 ? "x " + go : "";
    }

    private char lastMove() {
        char s;
        Move lastMove;
        if (board.history.isEmpty()) {
            lastMove = null;
        } else {
            lastMove = board.history.getLast();
        }
        if (lastMove == null) {
            s = ' ';
        } else if (lastMove.from.equals(pair)) {
            s = '-';
        } else if (lastMove.to.equals(pair)) {
            s = '+';
        } else {
            s = ' ';
        }
        return s;
    }

    private String toString(Piece piece) {
        String p;
        if (piece == null) {
            p = " ";
        } else {
            if (piece.type == PieceType.Pawn) {
                p = "P";
            } else {
                p = piece.type.toString();
            }
            p = piece.color > 0 ? p : p.toLowerCase();
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Square square = (Square) o;
        return pair.equals(square.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair);
    }

    // TODO it calculates if I place some unknown piece from nowhere, of piece is already there
    public int getExchangeScore(int color) {
        HashSet<Piece> potential = new HashSet<>();
        LinkedList<Waypoint> waypoints = new LinkedList<>();
        for (Waypoint waypoint : this.waypoints) {
            if (waypoint.isCapture()) {
                waypoints.add(waypoint);
                potential.add(waypoint.piece);
            }
        }
        waypoints.sort(Comparator.comparingInt(w -> w.piece.type.score));

        HashMap<Integer, LinkedList<Piece>> sides = new HashMap<>();
        HashSet<Piece> played = new HashSet<>();
        HashMap<Piece, HashSet<Piece>> waiting = new LinkedHashMap<>();

        for (Waypoint waypoint : waypoints) {
            Piece piece = waypoint.piece;
            if (played.containsAll(waypoint.obstructed)) {
                boolean progress = true;
                while (progress) {
                    progress = false;
                    played.add(piece);
                    sides.computeIfAbsent(piece.color, c -> new LinkedList<>()).add(piece);
                    for (HashSet<Piece> s : waiting.values()) {
                        s.remove(piece);
                    }
                    // TODO it can be even more complex: I don't choose dependent pieces, but player can modify the score by just keeping the adversary blocked
                    for (Iterator<Map.Entry<Piece, HashSet<Piece>>> iterator = waiting.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<Piece, HashSet<Piece>> entry = iterator.next();
                        if (entry.getValue().isEmpty()) {
                            piece = entry.getKey();
                            iterator.remove();
                            progress = true;
                            break;
                        }
                    }
                }
            } else if (potential.containsAll(waypoint.obstructed)) {
                waiting.put(piece, new HashSet<>(waypoint.obstructed));
            } else {
                Piece removed = piece;
                waiting.entrySet().removeIf(entry -> entry.getValue().contains(removed));
                potential.remove(piece);
            }
        }

        ArrayList<Piece> sequence = new ArrayList<>();
        int playingColor = color;
        while (true) {
            LinkedList<Piece> pieces = sides.get(playingColor);
            if (pieces.isEmpty()) {
                break;
            }
            sequence.add(pieces.removeFirst());
            playingColor *= -1;
        }

        sequence.remove(sequence.size() - 1);

        int score = 0;
        while (!sequence.isEmpty()) {
            if (score > 0) {
                score = 0;
            }
            Piece last = sequence.remove(sequence.size() - 1);
            score += last.type.score;
            score *= -1;
        }

        return score;
    }

    public boolean captures(Piece piece) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.captures(piece)) {
                return true;
            }
        }
        return false;
    }
}
