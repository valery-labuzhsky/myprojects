package server;

import java.net.ServerSocket;

/**
 * Created on 08.04.2020.
 *
 * @author ptasha
 */
public class ChessEngineServer {

    public static final int PORT = 3234;

    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                new ChessConnection(server.accept());
            }
        }
    }
}
