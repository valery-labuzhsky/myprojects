package board;

import board.pieces.Piece;

import java.util.*;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange {

    private HashSet<Piece> potential = new HashSet<>();
    private LinkedList<Waypoint> waypoints = new LinkedList<>();

    private final Square square;

    public Exchange(Square square) {
        this.square = square;
    }

    public int getScore(int color) {
        gatherWaypoints();
        waypoints.sort(Comparator.comparingInt(w -> w.piece.type.score));

        HashMap<Integer, LinkedList<Piece>> sides = new HashMap<>();
        HashSet<Piece> played = new HashSet<>();
        HashMap<Piece, HashSet<Piece>> waiting = new LinkedHashMap<>();

        for (Waypoint waypoint : waypoints) {
            Piece piece = waypoint.piece;
            HashSet<Piece> blocks = getBlocks(waypoint);
            if (played.containsAll(blocks)) {
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
            } else if (potential.containsAll(blocks)) {
                waiting.put(piece, blocks);
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

        if (square.piece != null) {
            score += square.piece.type.score;
        }
        return score;
    }

    protected HashSet<Piece> getBlocks(Waypoint waypoint) {
        return new HashSet<>(waypoint.getBlocks());
    }

    protected void gatherWaypoints() {
        for (Waypoint waypoint : square.waypoints) {
            if (waypoint.isAttack()) {
                addWaypoint(waypoint);
            }
        }
    }

    protected void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        potential.add(waypoint.piece);
    }
}
