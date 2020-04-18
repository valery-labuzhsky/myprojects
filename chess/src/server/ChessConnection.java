package server;

import board.IllegalMoveException;
import board.pieces.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created on 08.04.2020.
 *
 * @author ptasha
 */
public class ChessConnection extends Thread {
    private final Socket socket;

    public ChessConnection(Socket socket) {
        super(ChessConnection.class.getSimpleName());
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        try (socket) {
            System.out.println("Connected");
            PrintStream output = new PrintStream(socket.getOutputStream());
            Processor processor = new Processor(output);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = input.readLine();
            while (line != null && processor.process(line)) {
                line = input.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected");
    }

    private static class Processor {
        private final PrintStream output;
        private final Board board = new Board();

        public Processor(PrintStream output) {
            this.output = output;
        }

        public boolean process(String line) {
            try {
                System.out.println("[xboard]: " + line);
                String[] words = line.split(" ");
                if (words.length > 0) {
                    switch (words[0]) {
                        case "xboard":
                            respond("");
                            break;
                        case "protover":
                            respond("feature done=1");
                            break;
                        case "accepted":
                            break;
                        case "random":
                            break;
                        case "post":
                            // TODO turn on thinking output
                            break;
                        case "hard":
                            // TODO think on my turn
                            break;
                        case "computer":
                            // TODO I'm playing with computer
                            break;
                        case "quit":
                            return false;

                        case "level":
                            // TODO some time settings
                        case "time":
                            // TODO my time
                        case "otim":
                            // TODO opponent time
                            break;

                        case "new":
                            board.reset();
                            break;
                        case "force":
                            board.force();
                            break;
                        case "black":
                            board.black();
                            break;
                        case "white":
                            board.white();
                            break;
                        case "result":
                            if (line.contains("invalid move")) {
                                board.undo();
                            }
                            // TODO game has ended
                            break;

                        case "undo":
                            board.undo();
                            break;
                        case "go":
                            board.go();
                            makeMove();
                            break;
                        default:
                            if (!parseMove(line)) {
                                error("unknown command", line);
                            }
                    }
                } else {
                    error("failed to parse", line);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                error("internal error: " + e.getMessage(), line);
            }
            return true;
        }

        private boolean parseMove(String move) {
            try {
                if (!board.move(move)) {
                    return false;
                }
            } catch (IllegalMoveException e) {
                e.printStackTrace();
                illegalMove(e.getMessage(), move);
                return true;
            }
            makeMove();
            return true;
        }

        private void makeMove() {
            if (!board.force) {
                String response = board.move();
                if (response != null) {
                    respond(response);
                }
            }
        }

        private void illegalMove(String reason, String move) {
            if (reason == null) {
                illegalMove(move);
                return;
            }
            String response = "Illegal move (" + reason + "):" + move;
            System.err.println("[engine]: " + response);
            output.println(response);
        }

        private void illegalMove(String move) {
            String response = "Illegal move:" + move;
            System.err.println("[engine]: " + response);
            output.println(response);
        }

        private void error(String error, String line) {
            String response = "Error (" + error + "):" + line;
            System.err.println("[engine]: " + response);
            output.println(response);
        }

        private void respond(String response) {
            System.out.println("[engine]: " + response);
            output.println(response);
        }
    }
}
