package board.exchange;

import board.pieces.Piece;

import java.util.HashMap;

/**
 * Created on 22.11.2020.
 *
 * @author unicorn
 */
public abstract class CachedPieceCosts extends PieceCosts {
    final HashMap<Piece, Integer> costs = new HashMap<>();

    @Override
    void init(Piece piece) {
        Integer cost = calculate(piece);
        if (cost != 0) {
            costs.put(piece, cost);
        }
    }

    protected abstract Integer calculate(Piece piece);

    @Override
    int cost(Piece piece) {
        return costs.getOrDefault(piece, 0);
    }
}
