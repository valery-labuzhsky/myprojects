package board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class Pair {
    public final int file;
    public final int rank;

    public Pair(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public static int getFile(char c) {
        int x = c - 'a';
        if (x > 7) return -1;
        return x;
    }

    public static int parseRank(char c) {
        int y = c - '1';
        if (y > 7) return -1;
        return y;
    }

    public Pair go(int file, int rank) {
        return new Pair(this.file + file, this.rank + rank);
    }

    public String toString() {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }

    public Pair go(Pair direction) {
        return go(direction.file, direction.rank);
    }

    public boolean isValid() {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair) o;
        return file == pair.file &&
                rank == pair.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }

    public Logger log() {
        return LogManager.getLogger("" + toString());
    }
}
