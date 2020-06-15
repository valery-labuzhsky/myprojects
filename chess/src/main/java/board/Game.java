package board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created on 10.05.2020.
 *
 * @author unicorn
 */
public class Game implements Logged {
    public final Game previous;
    public final Action last;
    public final boolean permanent;

    private final int hash;

    public Game(Game previous, Action last, boolean permanent) {
        this.previous = previous;
        this.last = last;
        this.permanent = permanent;
        hash = Objects.hash(previous, last);
    }

    public boolean related(Game game) {
        return this.equals(game) || futureOf(game) || game.futureOf(this);
    }

    private boolean futureOf(Game game) {
        return previous != null && (previous.equals(game) || previous.futureOf(game));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Game game = (Game) o;
        return game.hash == hash &&
                Objects.equals(previous, game.previous) &&
                Objects.equals(last, game.last);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public Logger getLogger() {
        return permanent ?
                LogManager.getLogger("") :
                Logged.log(previous, last);
    }
}
