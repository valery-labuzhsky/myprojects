package board.math;

import board.Logged;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class Pair implements Logged {
    public final int file;
    public final int rank;

    public Pair(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public static int parseFile(char c) {
        int x = c - 'a';
        if (x > 7) {
            return -1;
        }
        return x;
    }

    public static int parseRank(char c) {
        int y = c - '1';
        if (y > 7) {
            return -1;
        }
        return y;
    }

    public static Pair parse(char file, char rank) {
        return new Pair(parseFile(file), parseRank(rank));
    }

    public Pair diagonal() {
        return new Pair(this.file + this.rank, this.file - this.rank);
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
        return isValid(this.file, this.rank);
    }

    public static boolean isValid(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
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

    @Override
    public Logger getLogger() {
        return LogManager.getLogger("" + toString());
    }

    public Pair step(Pair to) {
        int file = to.file - this.file;
        int rank = to.rank - this.rank;
        if (file == 0) {
            rank = Integer.signum(rank);
        } else if (rank == 0) {
            file = Integer.signum(file);
        } else if (Math.abs(file) == Math.abs(rank)) {
            file = Integer.signum(file);
            rank = Integer.signum(rank);
        }
        return new Pair(file, rank);
    }
}
