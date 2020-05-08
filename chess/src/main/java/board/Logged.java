package board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public interface Logged {
    Logger log();

    static Logger log(Logged... logs) {
        return LogManager.getLogger(Arrays.stream(logs).map(l -> l.log().getName()).collect(Collectors.joining(".")));
    }
}
