package board;

/**
 * Created on 10.05.2020.
 *
 * @author unicorn
 */
public class History {
    public Game game;

    public void newGame() {
        game = new Game(null, null, true);
    }

    public void push(Move move, boolean permanent) {
        game = new Game(game, move, permanent);
    }

    public Move pop() {
        try {
            return game.last;
        } finally {
            game = game.previous;
        }
    }

    public Move getLastMove() {
        return game.last;
    }
}
