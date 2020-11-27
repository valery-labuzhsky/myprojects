package board.exchange;

import board.Square;
import board.pieces.Piece;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange extends ExchangeState {
    private ExchangeResult result;

    public Exchange(Square square, int color) {
        this(square, color, new PieceCosts());
    }

    public Exchange(Square square, int color, PieceCosts costs) {
        super(square, color, costs);

        // TODO setScene is where cost calculation takes place
        //  it should probably be a separate entity
        //  but not this time
        setScene();
    }

    private Exchange(Exchange exchange) {
        super(exchange);
    }

    private void setScene() {
        HashMap<Integer, TreeSet<LinkedList<Piece>>> lines = new HashMap<>();
        Stream.of(-1, 1).forEach(c -> lines.put(c, new TreeSet<>(
                Comparator.comparing(l -> l.getFirst(), Comparator.<Piece>comparingInt(p -> (costs.cost(p) + p.cost()) * color).thenComparingInt(Object::hashCode))
        )));
        square.attackLines().forEach(s -> {
            LinkedList<Piece> line = new LinkedList<>();
            s.forEach(p -> {
                costs.init(p);
                line.add(p);
            });
            if (!line.isEmpty()) {
                lines.get(line.getFirst().color).add(line);
            }
        });

        int c = color;
        while (lines.values().stream().anyMatch(l -> !l.isEmpty())) {
            if (!lines.get(c).isEmpty()) {
                LinkedList<Piece> line = lines.get(c).pollFirst();
                sides.get(c).pieces.add(line.poll());
                if (!line.isEmpty()) {
                    lines.get(line.getFirst().color).add(line);
                }
            }
            c = -c;
        }
    }

    @Override
    public int getScore() {
        return getResult().getScore();
    }

    private ExchangeResult getResult() {
        if (result == null) {
            result = new ExchangeCalculator(this).calculate();
        }
        return result;
    }

    public Exchange move(Piece piece) {
        Exchange copy = new Exchange(this);
        copy.score -= copy.piece == null ? 0 : copy.piece.cost();
        copy.sides.get(piece.color).pieces.remove(piece);
        copy.piece = piece;
        copy.color = -color;
        return copy;
    }

    public Exchange remove(Piece piece) {
        Exchange copy = new Exchange(this);
        copy.sides.get(piece.color).pieces.remove(piece);
        return copy;
    }

    public Exchange add(Piece piece) {
        // TODO I need add it in accordance with cost
        Exchange copy = new Exchange(this);
        copy.sides.get(piece.color).pieces.add(piece);
        return copy;
    }

    public String toString() {
        return getResult().toString();
    }

}
