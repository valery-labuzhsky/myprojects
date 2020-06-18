package board.exchange;

import board.Remove;
import board.Square;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 12.06.2020.
 *
 * @author unicorn
 */
public class ComplexExchange extends Exchange {
    // TODO I need to cache exchanges themselves
    //  I'll need not separate exchange into simple and complex, but analyse complex situation
    //  in the future...

    // TODO here I need check score of removed pieces and calculate score with it
    public ComplexExchange(Square square, int color) {
        super(square, color);
    }

    @Override
    protected void setScene() {
        super.setScene();
        sides.values().forEach(s -> s.pieces.forEach(
                p -> costs.put(p, new RemoveWatcher(p, square).score())));
        sort();
    }

    private static class RemoveWatcher extends SimpleWatcher<Remove> {
        private final Square exclude;

        public RemoveWatcher(Piece piece, Square exclude) {
            super(new Remove(piece));
            this.exclude = exclude;
        }

        @Override
        public void collectBefore() {
            process(move.piece.whomAttack());
            process(move.piece.whomBlock());
        }

        public void process(Stream<Piece> pieces) {
            int color = move.piece.color;
            collect(pieces.filter(p -> p.color == color).filter(p -> p.square != exclude).
                    map(p -> () -> new Exchange(p.square, -color).result.score));
        }
    }
}
