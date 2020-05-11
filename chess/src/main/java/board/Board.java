package board;

import board.pieces.Piece;
import board.pieces.PieceType;
import board.situation.Situations;
import board.situation.Solution;

import java.util.*;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Board {
    private Square[][] squares;
    public final HashMap<Integer, ArrayList<Piece>> pieces = new HashMap<>();
    public int color;
    private Random random;
    public boolean force;
    public final History history = new History();
    public int score;

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
                    pieces.computeIfAbsent(piece.color, c -> new ArrayList<>()).add(piece);
                }
            }
        }

        color = -1;

        for (ArrayList<Piece> pieces : pieces.values()) {
            for (Piece piece : pieces) {
                piece.trace(new Waypoint.Origin(piece, piece.square));
            }
        }

        long seed = System.currentTimeMillis();
        System.out.println("Seed is " + seed);
        random = new Random(seed);

        force = false;
        history.newGame();
        score = 0;
    }

    public Square getSquare(Pair pair) {
        return getSquare(pair.file, pair.rank);
    }

    public Square getSquare(int file, int rank) {
        if (!Pair.isValid(file, rank)) {
            return null;
        }
        return squares[rank][file];
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
        Square from = move.from;
        Piece piece = from.piece;
        if (piece == null) {
            throw new IllegalMoveException("no piece on " + move.from);
        }

        Square dest = move.to;
        Waypoint waypoint = dest.waypoints.stream().
                filter(w -> w.piece == piece).
                findFirst().orElse(null);
        if (waypoint == null || !waypoint.moves()) {
            throw new IllegalMoveException();
        }

        if (dest.piece != null) {
            move.capture = dest.piece;
            dest.piece.remove();
        }

        piece.makeMove(move.to);
        history.push(move, true);

        for (Piece king : pieces.get(piece.color)) {
            if (king.type == PieceType.King) {
                if (king.isInDanger()) {
                    undo();
                    throw new IllegalMoveException("check");
                }
                break;
            }
        }

        System.out.println(this);
    }

    public void imagine(Move move) {
        Square from = move.from;
        Piece piece = from.piece;

        Square dest = move.to;

        if (dest.piece != null) {
            move.capture = dest.piece;
            dest.piece.remove();
        }

        piece.makeMove(move.to);
        history.push(move, false);
    }

    private Move parse(String move) {
        PieceType promotion = null;
        switch (move.length()) {
            case 4:
                break;
            case 5:
                promotion = PieceType.get(Character.toUpperCase(move.charAt(4)));
                break;
            default:
                return null;
        }
        Square from = getSquare(Pair.parse(move.charAt(0), move.charAt(1)));
        Square to = getSquare(Pair.parse(move.charAt(2), move.charAt(3)));
        if (from == null || to == null) {
            return null;
        }
        return new Move(from, to, promotion);
    }

    public boolean move(String line) throws IllegalMoveException {
        Move move = this.parse(line);
        if (move == null) {
            return false;
        }
        move(move);
        return true;
    }

    public String move() {
        Situations situations = new Situations(this);

        for (ArrayList<Piece> pieces : pieces.values()) {
            for (Piece piece : pieces) {
                situations.lookAt(piece); // TODO separate by colors
            }
        }

        situations.analyse();
        if (situations.isCheckmate()) {
            if (this.color == -1) {
                return "1-0 {White mates}";
            } else {
                return "0-1 {Black mates}";
            }
        }

        if (situations.result() < 0) {
            return "resign";
        }

        Waypoint badWaypoint = null;
        Move badMove = this.parse("g2g3");
        badWaypoint = badMove.to.waypoints.stream().filter(w -> w.piece.square.equals(badMove.from)).findFirst().orElse(null);
        Solution badSolution = null;

        List<Solution> moves = situations.getMoves();
        if (moves.isEmpty()) {
            Solution bestSolution = null;
            HashMap<Piece, ArrayList<Solution>> allMoves = new HashMap<>();
            for (Piece piece : pieces.get(color)) {
                for (Waypoint move : piece.getMoves()) {
                    Solution solution = new Solution(move);
                    if (move == badWaypoint) {
                        badSolution = solution;
                    }
                    if (bestSolution == null) {
                        bestSolution = solution;
                    } else if (bestSolution.compareTo(solution) < 0) {
                        allMoves.clear();
                        bestSolution = solution;
                    } else if (bestSolution.compareTo(solution) > 0) {
                        continue;
                    }
                    allMoves.computeIfAbsent(piece, p -> new ArrayList<>()).add(solution);
                }
            }

            if (!allMoves.isEmpty()) {
                Piece piece = allMoves.keySet().stream().skip(random.nextInt(allMoves.size())).findFirst().orElse(null);
                moves = allMoves.get(piece);
            }

            if (badSolution != null && badSolution.compareTo(bestSolution) == 0) {
                moves.add(badSolution);
            }
        }

        Move move;
        if (moves.contains(badSolution)) {
            move = badSolution.move;
        } else {
            move = moves.get(random.nextInt(moves.size())).move;
        }
        try {
            move(move);
            if (score * color < 0) {
                undo();
                return "resign";
            }

            return "move " + move.toString();
        } catch (IllegalMoveException e) {
            throw new RuntimeException("I made illegal move! " + move, e);
        }
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
                String string = square.toBoard();
                if (string.length() > length) {
                    length = string.length();
                }
                r.add(string);
            }
            strings.add(r);
        }
        int scale = 3;
        double l = Math.sqrt(length * 1.0 / scale);
        length = (int) Math.ceil(l);


        char[] horizontal = new char[length * scale];
        Arrays.fill(horizontal, ' ');

        char[] e = new char[length * scale];
        Arrays.fill(e, ' ');
        String empty = new String(e);

        StringBuilder out = new StringBuilder(score + "\n");
        for (ArrayList<String> row : strings) {
            for (int i = 0; i < 8; i++) {
                out.append(' ');
                out.append(horizontal);
            }
            out.append(' ').append('\n');
            for (int i = 0; i < length; i++) {
                for (int column = 0; column < row.size(); column++) {
                    String string = row.get(column);
                    out.append(' ');
                    if (string.length() >= length * scale) {
                        out.append(string, 0, length * scale);
                        row.set(column, string.substring(length * scale));
                    } else if (string.length() > 0) {
                        out.append(string);
                        out.append(empty, string.length(), length * scale);
                        row.set(column, "");
                    } else {
                        out.append(empty);
                    }
                }
                out.append(' ').append('\n');
            }
        }
        for (int i = 0; i < 8; i++) {
            out.append(' ');
            out.append(horizontal);
        }
        out.append(' ').append('\n');
        return out.toString();
    }

    public void white() {
        color = 1;
    }

    public void black() {
        color = -1;
    }

    public void undo() {
        Move move = history.pop();
        try {
            // TODO undo promotion
            Square from = move.to;
            if (from.piece == null) {
                throw new IllegalMoveException("no piece on " + move.to);
            }
            from.piece.makeMove(move.from);

            Piece piece = move.capture;
            if (piece != null) {
                piece.add(from);
            }

            System.out.println(this);
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
    }

}
