package board.situation;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public interface ScoreDiff {
    void before();

    void after();

    int getBefore();

    int getAfter();

    default int getScore() {
        return getAfter() - getBefore();
    }
}
