package board.situation;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public interface SimpleScoreDiff extends ScoreDiff {
    @Override
    default void before() {
    }

    @Override
    default void after() {
    }

    @Override
    default int getBefore() {
        return 0;
    }
}
