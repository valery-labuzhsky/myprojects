package board;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class Mark {
    Mark next;
    Piece piece;
    Square square;
    Set<Piece> obstructs = new HashSet<>();

    public Mark(Piece piece, Square square) {
        this.piece = piece;
        this.square = square;
        piece.marks.add(this);
        square.marks.add(this);
    }

    public Mark(Piece piece, Square square, Mark prev) {
        this(piece, square);
        if (prev != null) {
            prev.next = this;
            if (prev.square.piece != null) {
                this.obstructs = new ComboSet<>(prev.obstructs, prev.square.piece);
                prev.square.piece.marks.add(this);
            } else {
                this.obstructs = new ComboSet<>(prev.obstructs);
            }
        }
    }
}
