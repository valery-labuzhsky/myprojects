package board;

import board.pieces.Piece;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 27.04.2020.
 *
 * @author ptasha
 */
public class Blocks extends AbstractCollection<Piece> {

    private final Stream<Square> stream;

    public Blocks(Stream<Square> stream) {
        this.stream = stream;
    }

    @Override
    public Iterator<Piece> iterator() {
        return stream.
                map(s -> s.piece).
                filter(Objects::nonNull).iterator();
    }

    @Override
    public int size() {
        int size = 0;
        for (Iterator<Piece> iterator = this.iterator(); iterator.hasNext(); size++) {
            iterator.next();
        }
        return size;
    }
}
