package board;

import board.exchange.ComplexExchange;
import board.exchange.Exchange;

import java.util.HashMap;

/**
 * Created on 10.05.2020.
 *
 * @author unicorn
 */
public class Scores {
    public final Square square;

    private Game permanent;
    private final HashMap<Game, Cache> caches = new HashMap<>();

    public Scores(Square square) {
        this.square = square;
    }

    public Exchange.Result getResult(int color) {
        return getExchange(color).result;
    }

    public Exchange getExchange(int color) {
        Game current = square.board.history.game;
        if (current.permanent && !current.equals(permanent)) {
            caches.keySet().removeIf(cached -> !cached.related(current));
            permanent = current;
        }
        return caches.computeIfAbsent(current, g -> new Cache()).getExchange(color);
    }

    public void clear() {
        caches.clear();
    }

    private class Cache {
        HashMap<Integer, Exchange> scores = new HashMap<>();

        public Exchange getExchange(int color) {
            return scores.computeIfAbsent(color, c -> new ComplexExchange(square, color));
        }
    }
}
