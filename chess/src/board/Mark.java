package board;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class Mark {
    Mark next;
    Mark prev;
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
            this.prev = prev;
            if (prev.square.piece != null) {
                this.obstructs = new ComboSet<>(prev.obstructs, prev.square.piece);
                prev.square.piece.marks.add(this);
            } else {
                this.obstructs = new ComboSet<>(prev.obstructs);
            }
        }
    }

    public void enrich(List<Move> moves) {
        Move move = move();
        if (move != null) {
            moves.add(move);
        }
    }

    public Move move() {
        return this.piece.move(this);
    }

    public boolean captures(Piece piece) {
        if (this.piece.color != piece.color) {
            if (piece.captures(this)) {
                if (obstructs.isEmpty()) {
                    return true;
                } else if (obstructs.size() == 1) {
                    return obstructs.contains(piece);
                }
            }
        }
        return false;
    }
}
