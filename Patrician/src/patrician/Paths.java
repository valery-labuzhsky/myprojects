package patrician;

import sun.security.krb5.internal.PAData;

import java.io.File;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class Paths {
    public static final String SAVE = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/";
    public static final String ROUTES = SAVE + "AutoRoute/";
    public static final String GAMES = SAVE + "Kam/";

    public static File getGame(String name) {
        return new File(GAMES, name+".pat");
    }

    public static File getRoute(String name) {
        return new File(ROUTES, name+".rou");
    }
}
