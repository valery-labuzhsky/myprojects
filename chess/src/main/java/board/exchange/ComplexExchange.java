package board.exchange;

import board.Remove;
import board.Square;
import board.pieces.Piece;

import java.util.HashMap;
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
    private final HashMap<Piece, Integer> costs = new HashMap<>();

    // TODO here I need check score of removed pieces and calculate score with it
    public ComplexExchange(Square square, int color) {
        super(square, color);
    }

    @Override
    protected void setScene() {
        super.setScene();
        // TODO calculate cost for every piece
        //  will my watcher be useful after all?
        //  but I don't need to calculate complex score at least for now

        // TODO how to calculate all the exchanges?
        //  I need to count on the free pieces then I choose what each piece must do
        //  I really need a case before doing anything
        //  it must be as case specific as reasonably to do it

        sides.values().stream().flatMap(s -> s.pieces.stream()).
                forEach(p -> costs.put(p, calcCost(p)));
    }

    private int calcCost(Piece piece) {
        return piece.cost() + new RemoveWatcher(piece).score();
    }

    @Override
    protected int cost(Piece piece) {
        return costs.get(piece);
    }

    private static class RemoveWatcher extends SimpleWatcher<Remove> {
        public RemoveWatcher(Piece piece) {
            super(new Remove(piece));
        }

        @Override
        public void collectBefore() {
            process(move.piece.whomAttack());
            process(move.piece.whomBlock());
        }

        public void process(Stream<Piece> pieces) {
            int color = move.piece.color;
            collect(pieces.filter(p -> p.color == color).
                    map(p -> () -> new Exchange(p.square, -color).getResult().score));
        }

    }
}
