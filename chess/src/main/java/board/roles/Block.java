package board.roles;

import board.pieces.Piece;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public class Block extends Role {
    public final Attack attack;

    public Block(Piece piece, Piece whom, Piece from) {
        super(piece);
        this.attack = Attack.create(from, whom);
    }

    @Override
    public String toString() {
        // TODO to be replaced with class hierarchy
        if (piece.color == attack.piece.color) {
            return piece + " obstructs " + attack.toContinuous();
        } else {
            return piece + " blocks " + attack.toContinuous();
        }
    }

    @Override
    public boolean isMeaningful() {
        return attack.isMeaningful();
    }

}
