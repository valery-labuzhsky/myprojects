import server.ChessEngineServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * Created on 07/04/2020.
 *
 * @author ptasha
 */
public class ChessEngineClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", ChessEngineServer.PORT)) {
            CountDownLatch barrier = new CountDownLatch(1);
            Thread thread = new Thread(ChessEngineClient.class.getSimpleName() + "Connection") {
                @Override
                public void run() {
                    barrier.countDown();
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintStream output = System.out;
                        String line;
                        line = input.readLine();
                        while (line != null) {
                            output.println(line);
                            line = input.readLine();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            barrier.await();
            PrintStream output = new PrintStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String line = input.readLine();
            while (!Thread.currentThread().isInterrupted()) {
                output.println(line);
                if ("quit".equals(line)) {
                    thread.join();
                    return;
                }
                line = input.readLine();
            }
        }
    }
}
