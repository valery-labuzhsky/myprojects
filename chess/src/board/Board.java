package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Board {
    private Square[][] squares;
    public final ArrayList<Piece> pieces = new ArrayList<>();
    public int color;
    private Random random;
    public boolean force;
    public Move lastMove;

    public Board() {
        reset();
    }

    public void reset() {
        pieces.clear();
        Piece[][] board = new Piece[][]{
                {w().R(), w().N(), w().B(), w().Q(), w().K(), w().B(), w().N(), w().R()},
                {w().P(), w().P(), w().P(), w().P(), w().P(), w().P(), w().P(), w().P()},
                {blank(), blank(), blank(), blank(), blank(), blank(), blank(), blank()},
                {blank(), blank(), blank(), blank(), blank(), blank(), blank(), blank()},
                {blank(), blank(), blank(), blank(), blank(), blank(), blank(), blank()},
                {blank(), blank(), blank(), blank(), blank(), blank(), blank(), blank()},
                {b().P(), b().P(), b().P(), b().P(), b().P(), b().P(), b().P(), b().P()},
                {b().R(), b().N(), b().B(), b().Q(), b().K(), b().B(), b().N(), b().R()},
        };

        squares = new Square[][]{
                new Square[8],
                new Square[8],
                new Square[8],
                new Square[8],
                new Square[8],
                new Square[8],
                new Square[8],
                new Square[8],
        };

        for (int rank = 0; rank < squares.length; rank++) {
            for (int file = 0; file < squares[rank].length; file++) {
                Piece piece = board[rank][file];
                Square square = new Square(this, new Pair(file, rank));
                squares[rank][file] = square;
                if (piece != null) {
                    square.piece = piece;
                    piece.square = square;
                    pieces.add(piece);
                }
            }
        }

        color = -1;

        for (Piece piece : pieces) {
            piece.marksOn();
        }

        long seed = System.currentTimeMillis();
        System.out.println("Seed is " + seed);
        random = new Random(seed);

        force = false;
        lastMove = null;
    }

    public Square getSquare(Pair pair) {
        return squares[pair.rank][pair.file];
    }

    private Piece blank() {
        return null;
    }

    private PieceBuilder w() {
        return new PieceBuilder(this, 1);
    }

    private PieceBuilder b() {
        return new PieceBuilder(this, -1);
    }

    public void move(Move move) throws IllegalMoveException {
        lastMove = move;
        System.out.println(this);
        // TODO check legality like piece can go there at all
        Square from = getSquare(move.from);
        if (from.piece == null) {
            throw new IllegalMoveException("no piece on " + move.from);
        }
        from.piece.move(this, move.to);
    }

    public boolean move(String line) throws IllegalMoveException {
        Move m = Move.parse(line);
        if (m == null) {
            return false;
        }
        move(m);
        return true;
    }

    public Move move() {
        ArrayList<Piece> myPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece.color == color && piece.canMove()) {
                myPieces.add(piece);
            }
        }

        Piece piece = myPieces.get(random.nextInt(myPieces.size()));
        List<Move> moves = piece.getMoves();

        Move move = moves.get(random.nextInt(moves.size()));
        try {
            move(move);
        } catch (IllegalMoveException e) {
            throw new RuntimeException("I made illegal move! " + move, e);
        }

        return move;
    }

    public void go() {
        this.force = false;
    }

    public void force() {
        this.force = true;
    }

    public String toString() {
        ArrayList<ArrayList<String>> strings = new ArrayList<>();
        int length = 0;
        for (int i = squares.length - 1; i >= 0; i--) {
            ArrayList<String> r = new ArrayList<>();
            Square[] row = squares[i];
            for (Square square : row) {
                String string = square.toString();
                if (string.length() > length) {
                    length = string.length();
                }
                r.add(string);
            }
            strings.add(r);
        }
        double l = Math.sqrt(length * 0.5);
        length = (int) Math.ceil(l);


        char[] horizontal = new char[length * 2];
        Arrays.fill(horizontal, '-');

        char[] e = new char[length * 2];
        Arrays.fill(e, ' ');
        String empty = new String(e);

        StringBuilder out = new StringBuilder();
        for (ArrayList<String> row : strings) {
            for (int i = 0; i < 8; i++) {
                out.append('|');
                out.append(horizontal);
            }
            out.append('|').append('\n');
            for (int i = 0; i < length; i++) {
                for (int column = 0; column < row.size(); column++) {
                    String string = row.get(column);
                    out.append('|');
                    if (string.length() >= length * 2) {
                        out.append(string, 0, length * 2);
                        row.set(column, string.substring(length * 2));
                    } else if (string.length() > 0) {
                        out.append(string);
                        out.append(empty, string.length(), length * 2);
                        row.set(column, "");
                    } else {
                        out.append(empty);
                    }
                }
                out.append('|').append('\n');
            }
        }
        for (int i = 0; i < 8; i++) {
            out.append('|');
            out.append(horizontal);
        }
        out.append('|').append('\n');
        return out.toString();
    }

    public void white() {
        color = 1;
    }

    public void black() {
        color = -1;
    }
}
