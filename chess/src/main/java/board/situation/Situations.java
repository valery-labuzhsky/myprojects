package board.situation;

import board.Board;
import board.Logged;
import board.Waypoint;
import board.pieces.Piece;
import board.pieces.PieceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    public final ArrayList<Solution> moves = new ArrayList<>();

    public final HashSet<Solution> solutions = new HashSet<>();

    public final Board board;
    public Situation check;
    public int score;
    private int defenceScore;

    public Situations(Board board) {
        this.board = board;
    }

    public int result() {
        return this.score + this.getDefenceScore();
    }

    public void lookAt(Piece piece) {
        for (Waypoint waypoint : piece.square.waypoints) {
            if (waypoint.captures()) {
                Situation situation = new Situation(piece, this.board.color);
                solutions.addAll(situation.solutions);
                log().info(situation);
                if (piece.type == PieceType.King) {
                    check = situation;
                }
                score += situation.score();
                break;
            }
        }

//        piece.log().info(piece.square.getExchangeResult(-piece.color));
    }

    public boolean isCheckmate() {
        return check != null && result() < -PieceType.King.score / 2;
    }

    public List<Solution> getMoves() {
        return this.moves;
    }

    public void analyse() {
        moves.addAll(Solution.best(solutions, board.color));

        moves.stream().findFirst().ifPresent(m -> defenceScore = m.getDefence());

        System.out.println("Total: " + score);
        System.out.println("Defend: " + getDefenceScore() + " " + Logged.tabs(this.moves));

        for (Solution move : moves) {
            log().info("Retaliation " + new RetaliationScore(move.move));
        }
    }

    private Logger log() {
        return LogManager.getLogger("situations");
    }

    public int getDefenceScore() {
        return defenceScore;
    }
}
