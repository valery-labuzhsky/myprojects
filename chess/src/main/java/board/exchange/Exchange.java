package board.exchange;

import board.Square;
import board.math.Ray;
import board.pieces.Knight;
import board.pieces.Piece;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange extends ExchangeState {
    private ExchangeResult result;

    public Exchange(Square square, int color) {
        this(square, color, p -> 0);
    }

    public Exchange(Square square, int color, Function<Piece, Integer> costs) {
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
        Ray.stream().forEach(ray ->
                ray.ray(square).
                        map(s -> s.piece).filter(Objects::nonNull).
                        takeWhile(p -> p.isAttack(p.square, square)).
                        map(p -> new EPiece(p, costs.apply(p), ray, ray.distance(square, p.square))).
                        forEach(piece -> sides.get(piece.piece.color).pieces.add(piece))
        );
        Knight.getMoves(square).
                map(s -> s.piece).filter(Objects::nonNull).
                filter(p -> p.isAttack(p.square, square)).
                map(p -> new EPiece(p, costs.apply(p))).
                forEach(piece -> sides.get(piece.piece.color).pieces.add(piece));
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
        return new Exchange(this).moveMe(piece);
    }

    public Exchange remove(Piece piece) {
        return new Exchange(this).removeMe(piece);
    }

    public Exchange add(Piece piece) {
        return new Exchange(this).addMe(piece);
    }

    private Exchange moveMe(Piece piece) {
        score -= this.piece == null ? 0 : this.piece.cost();
        removeMe(piece);
        this.piece = piece;
        color = -color;
        return this;
    }

    private Exchange removeMe(Piece piece) {
        sides.get(piece.color).pieces.removeIf(p -> p.piece == piece);
        return this;
    }

    private Exchange addMe(Piece piece) {
        sides.get(piece.color).pieces.add(new EPiece(piece));
        return this;
    }

    public String toString() {
        return getResult().toString();
    }
}
