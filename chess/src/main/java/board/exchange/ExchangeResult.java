package board.exchange;

import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public class ExchangeResult extends ExchangeState {
    ExchangeResult(ExchangeState exchange) {
        super(exchange);
    }

    @Override
    protected Side newSide(ExchangeState.Side side) {
        return new Side(side);
    }

    @Override
    protected Side getSide(int color) {
        return (Side) super.getSide(color);
    }

    protected class Side extends ExchangeState.Side {
        ArrayList<Piece> lost = new ArrayList<>();

        public Side(ExchangeState.Side side) {
            super(side);
            if (side instanceof Side) {
                lost.addAll(((Side) side).lost);
            }
        }
    }

    public String toString() {
        ArrayList<Piece> wlost = getSide(1).lost;
        ArrayList<Piece> blost = getSide(-1).lost;
        if (wlost.isEmpty() && blost.isEmpty()) {
            return "free";
        } else if (wlost.isEmpty()) {
            return blost.get(0) + " loss = " + getScore();
        } else if (blost.isEmpty()) {
            return wlost.get(0) + " loss = " + getScore();
        } else {
            return wlost + " x " + blost + " = " + getScore();
        }
    }
}
