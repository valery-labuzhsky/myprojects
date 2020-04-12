package board;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException(String message) {
        super(message);
    }
}
