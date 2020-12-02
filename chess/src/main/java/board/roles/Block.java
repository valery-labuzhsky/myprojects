package board.roles;

import board.pieces.Piece;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public class Block extends Role {
    public final BlockedTarget attack;

    public Block(Piece piece, Piece whom, Piece from) {
        super(piece);
        this.attack = BlockedTarget.create(from, whom);
    }

    @Override
    public int getScore() {
        return attack.getScore();
    }

    @Override
    public Piece getWhom() {
        return attack.getWhom();
    }

    @Override
    public String toString() {
        // TODO to be replaced with class hierarchy
        if (piece.color == attack.piece.color) {
            return piece + " obstructs " + attack;
        } else {
            return piece + " blocks " + attack;
        }
    }

}
