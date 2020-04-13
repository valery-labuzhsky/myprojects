package board;

import java.util.*;

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
    public final LinkedList<Move> history = new LinkedList<>();
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
        history.clear();
        score = 0;
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
        // TODO check legality like piece can go there at all
        Square from = getSquare(move.from);
        if (from.piece == null) {
            throw new IllegalMoveException("no piece on " + move.from);
        }

        Square dest = getSquare(move.to);
        if (dest.piece != null) {
            if (dest.piece.color == from.piece.color) {
                throw new IllegalMoveException("own color");
            }
            move.capture = dest.piece;
            dest.piece.remove();
        }

        from.piece.move(this, move.to);
        history.add(move);
        System.out.println(this);
    }

    public boolean move(String line) throws IllegalMoveException {
        Move move = Move.parse(line);
        if (move == null) {
            return false;
        }
        move(move);
        return true;
    }

    public String move() {
        King king = null;

        ArrayList<Piece> myPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece.color == color) {
                if (piece.type == PieceType.King) {
                    king = (King) piece;
                }
                myPieces.add(piece);
            }
        }
        ArrayList<Waypoint> danger = new ArrayList<>();
        assert king != null;
        for (Waypoint waypoint : king.square.waypoints) {
            if (waypoint.captures(king)) {
                danger.add(waypoint);
            }
        }

        List<Move> moves = new ArrayList<>();
        if (danger.size() == 0) {
            int bestScore = 0;
            HashMap<Piece, ArrayList<Move>> allMoves = new HashMap<>();
            for (Piece piece : myPieces) {
                for (Move move : piece.getMoves()) {
                    Square square = getSquare(move.to);
                    Piece capture = square.piece;
                    int score = 0;
                    if (capture != null) {
                        score += capture.type.score;
                    }
                    for (Waypoint waypoint : square.waypoints) {
                        if (waypoint.captures(piece)) {
                            score -= piece.type.score;
                            break;
                        }
                    }

                    if (score >= bestScore) {
                        if (score > bestScore) {
                            bestScore = score;
                            allMoves.clear();
                        }
                        allMoves.computeIfAbsent(piece, p -> new ArrayList<>()).add(move);
                    }
                }
            }

            if (!allMoves.isEmpty()) {
                Piece piece = allMoves.keySet().stream().skip(random.nextInt(allMoves.size())).findFirst().orElse(null);
                moves = allMoves.get(piece);
            }
        } else {
            moves.addAll(king.getMoves());
            if (danger.size() == 1) {
                Waypoint waypoint = danger.get(0);
                for (Waypoint capture : waypoint.piece.square.waypoints) {
                    if (capture.captures(waypoint.piece)) {
                        capture.enrich(moves);
                    }
                }
                while (waypoint.prev != null) {
                    waypoint = waypoint.prev;
                    for (Waypoint protect : waypoint.square.waypoints) {
                        if (protect.piece.color == color) {
                            protect.enrich(moves);
                        }
                    }

                }
            }
        }

        // TODO just check if it's there!
//        for (Move move : moves) {
//            if (move.toString().equals("e3e4")) {
//                try {
//                    move(move);
//                    return "move " + move.toString();
//                } catch (IllegalMoveException e) {
//                    throw new RuntimeException("I made illegal move! " + move, e);
//                }
//            }
//        }

        if (score * color < 0) {
            return "resign";
        }

        if (moves.isEmpty()) {
            if (this.color == -1) {
                return "1-0 {White mates}";
            } else {
                return "0-1 {Black mates}";
            }
        } else {
            Move move = moves.get(random.nextInt(moves.size()));
            try {
                move(move);
                return "move " + move.toString();
            } catch (IllegalMoveException e) {
                throw new RuntimeException("I made illegal move! " + move, e);
            }
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

        StringBuilder out = new StringBuilder(score + "\n");
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

    public void undo() {
        Move move = history.removeLast();
        try {
            // TODO undo promotion
            Square from = getSquare(move.to);
            if (from.piece == null) {
                throw new IllegalMoveException("no piece on " + move.to);
            }
            from.piece.move(this, move.from);

            Piece piece = move.capture;
            if (piece != null) {
                piece.add(getSquare(move.to));
            }

            System.out.println(this);
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
    }

}
