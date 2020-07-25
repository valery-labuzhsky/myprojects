package board.pieces;

import board.*;
import board.exchange.Exchange;
import board.roles.Attack;
import board.roles.Block;
import board.roles.Role;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public abstract class Piece implements Logged {
    public final Board board;
    public final int color;
    public final PieceType type;
    public Square square;

    public Piece(PieceType type, Board board, int color) {
        this.board = board;
        this.color = color;
        this.type = type;
        int c = 0;
    }

    //region Is From To
    public boolean isGo(Square from, Square to) {
        return isMove(from, to);
    }

    public boolean isAttack(Square from, Square to) {
        return isMove(from, to);
    }

    public abstract boolean isMove(Square from, Square to);
    //endregion

    //region Can From To
    public boolean canAttack(Square from, Square to) {
        return isAttack(from, to) && getBlocks(from, to).isEmpty();
    }

    private boolean canMove(Square to) {
        return canMove(square, to);
    }

    public boolean canMove(Square from, Square to) {
        return (to.piece == null || to.piece.color != color) && isMove(from, to) && getBlocks(from, to).isEmpty();
    }
    //endregion

    //region Plan Attacks
    public abstract Stream<Square> planPotentialAttacks(Square to);

    public Stream<Square> planAttackSquares(Square to) {
        return planPotentialAttacks(to).
                filter(this::canMove).
                filter(s -> canAttack(s, to));
    }

    public Stream<Move> planAttacks(Square to) {
        return planAttackSquares(to).map(this::move);
    }

    Stream<Square> planPotentialAttacks(Square square, XY.Transform... transforms) {
        XY from = new XY();
        XY to = new XY();
        return Stream.of(transforms).
                flatMap(t -> {
                    from.set(this.square.pair);
                    to.set(square.pair);
                    t.transform(from);
                    t.transform(to);
                    from.swap(to);
                    if (t.back(from) && t.back(to)) {
                        return Stream.of(board.getSquare(from.x, from.y), board.getSquare(to.x, to.y)).filter(Objects::nonNull);
                    }
                    return Stream.empty();
                });
    }
    //endregion

    //region Where To
    public Stream<Square> whereToMove() {
        return Stream.concat(whereToGo(), whomToCapture().map(p -> p.square));
    }

    public abstract Stream<Square> whereToGo();
    //endregion

    //region Whom
    public abstract Stream<Piece> whomAttack();

    private Stream<Piece> whomToCapture() {
        return whomAttack().filter(p -> p.color == -color);
    }

    public Stream<Piece> whomBlock() {
        return blockRoles().map(b -> b.attack.whom);
    }

    public ArrayList<Piece> enemies() {
        return new ArrayList<>(board.pieces.get(-color));
    }

    public Collection<Piece> friends() {
        return new ArrayList<>(board.pieces.get(color)); // TODO what else? CopyOnWrite?
    }
    //endregion

    //region Roles
    public Stream<Role> roles() {
        return Stream.concat(attackRoles(), blockRoles());
    }

    public Stream<Role> meaningfulRoles() {
        return roles().filter(r -> r.getScore() != 0);
    }

    public Stream<Attack> attackRoles() {
        return whomAttack().map(p -> Attack.create(this, p));
    }

    public Stream<Block> blockRoles() {
        Stream.Builder<Block> stream = Stream.builder();

        blockRoles(stream, Piece::isRookOrQueen, 1, 0);
        blockRoles(stream, Piece::isRookOrQueen, 0, 1);
        blockRoles(stream, Piece::isBishopOrQueen, 1, 1);
        blockRoles(stream, Piece::isBishopOrQueen, -1, 1);

        return stream.build();
    }

    private void blockRoles(Stream.Builder<Block> stream, Predicate<Piece> attack, int file, int rank) {
        Piece p1 = square.findPieceOnRay(file, rank);
        Piece p2 = square.findPieceOnRay(-file, -rank);
        if (p1 != null && p2 != null) {
            if (attack.test(p1)) {
                stream.add(new Block(this, p2, p1));
            }
            if (attack.test(p2)) {
                stream.add(new Block(this, p1, p2));
            }
        }
    }
    //endregion

    //region Status
    public Exchange getExchange() {
        return square.scores.getExchange(-color);
    }

    private boolean isRookOrQueen() {
        return type == PieceType.Rook || type == PieceType.Queen;
    }

    private boolean isBishopOrQueen() {
        return type == PieceType.Bishop || type == PieceType.Queen;
    }

    public int cost() {
        return type.score * color;
    }

    int border() {
        return (7 - color * 7) / 2;
    }

    public boolean onBoard() {
        return this.square.piece == this;
    }

    public boolean isInDanger() {
        return this.square.captures(this);
    }
    //endregion

    //region Moving
    public Move move(Square to) {
        if (square.piece == null) {
            throw new IllegalStateException(this + ": " + square);
        }
        return new Move(this.square, to);
    }

    public void makeMove(Square to) {
        // TODO it may be en passant
        square.piece = null;
        marksOff();
        put(to);
    }

    private void put(Square square) {
        this.square = square;
        this.square.piece = this;
        trace(new Waypoint.Origin(this, this.square));
    }

    public void add(Square square) {
        board.score += cost();
        board.pieces.get(color).add(this);
        put(square);
    }

    public void remove() {
        this.square.piece = null;
        board.pieces.get(color).remove(this);
        marksOff();
        board.score -= cost();
    }

    public abstract void trace(MovesTracer tracer);

    private void marksOff() {
        while (!waypoints.isEmpty()) {
            waypoints.iterator().next().remove();
        }
    }
    //endregion

    //region Technical
    public String getLetter() {
        return color > 0 ? type.getLetter() : type.getLetter().toLowerCase();
    }

    @Override
    public String toString() {
        return "" + getLetter() + square.pair;
    }

    @Override
    public Logger getLogger() {
        return square.getLogger();
    }
    //endregion


    //region Old blocks
    public Collection<Piece> getBlocks(Square from) {
        return getBlocks(from, this.square);
    }

    // TODO get rid of blocks
    public Collection<Piece> getBlocks(Square from, Square to) {
        return new Blocks(() -> from.ray(to));
    }
    //endregion

    //region Waypoints
    public final HashSet<Waypoint> waypoints = new HashSet<>();

    public boolean goes(Waypoint waypoint) {
        return true;
    }

    public boolean attacks(Waypoint waypoint) {
        return true;
    }
    //endregion

}
