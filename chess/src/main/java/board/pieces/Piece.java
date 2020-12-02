package board.pieces;

import board.Board;
import board.Logged;
import board.Move;
import board.Square;
import board.exchange.Exchange;
import board.roles.*;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.abs;

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
        return isAttack(from, to) && hasBlocks(from, to);
    }

    private boolean canMove(Square to) {
        return canMove(square, to);
    }

    public boolean canMove(Square from, Square to) {
        return (to.piece == null || to.piece.color != color) && isMove(from, to) && hasBlocks(from, to);
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
    //endregion

    //region Block
    public Stream<Move> block(Piece piece, Piece enemy) {
        Square from = piece.square;
        Square to = enemy.square;
        if (abs(from.pair.file - to.pair.file) < 2) return Stream.empty();
        if (abs(from.pair.rank - to.pair.rank) < 2) return Stream.empty();
        return planPotentialBlock(from, to).filter(this::canMove).map(this::move);
    }

    protected abstract Stream<Square> planPotentialBlock(Square friend, Square enemy);
    //endregion

    //region Where To
    public Stream<Square> whereToMove() {
        return Stream.concat(whereToGo(), whomToAttack().map(p -> p.square));
    }

    public abstract Stream<Square> whereToGo();
    //endregion

    //region Whom
    public List<Piece> enemiesList() {
        return enemies().collect(Collectors.toList());
    }

    public Stream<Piece> enemies() {
        return board.pieces.get(-color).stream();
    }

    public Stream<Piece> friends() {
        return board.pieces.get(color).stream().filter(p -> p != this);
    }

    public abstract Stream<Piece> whomTarget();

    private Stream<Piece> whomToAttack() {
        return whomTarget().filter(p -> p.color == -color);
    }

    public Stream<Piece> whomBlock() {
        return blockRoles().map(b -> b.attack.whom);
    }

    public Stream<CanTarget> whoCanTarget() {
        return Stream.concat(whoCanAttack(), whoCanProtect());
    }

    public Stream<CanAttack> whoCanAttack() {
        return enemies().flatMap(p -> p.planAttackSquares(square).map(s -> new CanAttack(p, this, s)));
    }

    public Stream<CanProtect> whoCanProtect() {
        return friends().flatMap(p -> p.planAttackSquares(square).map(s -> new CanProtect(p, this, s)));
    }
    //endregion

    //region Roles
    public Stream<Role> roles() {
        return Stream.concat(targetRoles(), blockRoles());
    }

    public Stream<Role> meaningfulRoles() {
        return roles().filter(r -> r.getScore() != 0);
    }

    public Stream<Target> targetRoles() {
        return whomTarget().map(p -> Target.create(this, p));
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
        put(to);
    }

    private void put(Square square) {
        this.square = square;
        this.square.piece = this;
    }

    public void add(Square square) {
        board.score += cost();
        board.pieces.get(color).add(this);
        put(square);
    }

    public void remove() {
        this.square.piece = null;
        board.pieces.get(color).remove(this);
        board.score -= cost();
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
    // TODO get rid of blocks
    public Stream<Piece> getBlocks(Square from, Square to) {
        return from.ray(to).map(s -> s.piece).filter(Objects::nonNull);
    }

    public boolean hasBlocks(Square from, Square to) {
        return getBlocks(from, to).findAny().isEmpty();
    }
    //endregion
}
