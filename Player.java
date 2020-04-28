import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Player {
    private final int EMPTY = 0;
    private final int WHITE = 1;
    private final int BLACK = -1;
    private final int BOARD_SIZE = 8;
    public int player_turn = BLACK;
    public int my_color;
    private int player_won;
    private boolean skip_turn = false;

    private int board[][];
    private int possible_moves[][];

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public Player() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        possible_moves = new int[BOARD_SIZE][BOARD_SIZE];
        player_won = EMPTY;
    }

    public void connectToServer() {
        int tmp_id;
        System.out.println("Connecting to server at localhost:60065");
        try {
            socket = new Socket("localhost", 60065);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            tmp_id = dataIn.readInt();
            if (tmp_id == 1) {
                my_color = BLACK;
                System.out.println("Connected to the server as player BLACK.");
            } else {
                my_color = WHITE;
                System.out.println("Connected to the server as player WHITE.");
            }
        } catch (IOException e) {
            System.out.println("IOException from ConnectToServer.");
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Cannot close connection.");
        }
    }

    public void requestBoardState() {
        System.out.println("Sending request for board state.");
        try {
            dataOut.writeInt(my_color);
            dataOut.flush();
        } catch (IOException e) {
            System.out.println("Cannot connect to the server.");
        }

        System.out.println("Reading board");
        try {
            for (int i = 0; i < BOARD_SIZE; ++i)
                for (int j = 0; j < BOARD_SIZE; ++j) {
                    board[i][j] = dataIn.readInt();
                }
        } catch (IOException e) {
            System.out.println("IOException cannot read board state from server.");
        }
    }

    public void requestValidMoves() {
        System.out.println("Sending request for possible moves.");
        try {
            dataOut.writeInt(my_color);
            dataOut.flush();
            skip_turn = dataIn.readBoolean();
        } catch (IOException e) {
            System.out.println("Cannot connect to the server.");
        }
        if (!skip_turn) {
            System.out.println("Reading moves");
            try {
                for (int i = 0; i < BOARD_SIZE; ++i)
                    for (int j = 0; j < BOARD_SIZE; ++j) {
                        possible_moves[i][j] = dataIn.readInt();
                    }
            } catch (IOException e) {
                System.out.println("IOException cannot read possible moves from server.");
            }
            displayValidMoves();
        }
    }

    public void displayBoard() {
        System.out.println("BOARD STATE");
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", board[i][j]);
            }
            System.out.println();
        }
    }

    public void displayValidMoves() {
        System.out.println("POSSIBLE MOVES");
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", possible_moves[i][j]);
            }
            System.out.println();
        }
    }

    public void move(int x, int y) {
        if (!skip_turn) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.flush();
                player_turn *= -1;
            } catch (IOException e) {
                System.out.println("IOException, cannot send move to server.");
            }
        }
    }

    public void debug_move() {
        if (!skip_turn) {
            int x;
            int y;
            Scanner scan = new Scanner(System.in);
            do {
                System.out.println("input 2 cooardinates: ");
                x = scan.nextInt();
                y = scan.nextInt();
            } while (possible_moves[x][y] != 1);
            move(x, y);
        }
    }

    public void waitForTurn() {
        try {
            player_turn = dataIn.readInt();
            if (player_turn == 100) {
                player_won = -my_color;
                System.out.println("UPS i lost");
            }
            System.out.println("My turn " + (player_turn == my_color));
        } catch (IOException e) {
        }
    }

    public void checkForWin() {
        System.out.println("Sending request for win conditin.");
        try {
            dataOut.writeInt(my_color);
            dataOut.flush();
        } catch (IOException e) {
            System.out.println("Cannot connect to the server.");
        }
        try {
            System.out.println("Checking if i won.");
            player_won = dataIn.readInt();
        } catch (IOException e) {
            System.out.println("Cannot read winner info.");
        }
    }

    public static void main(String[] args) {
        Player player = new Player();
        player.connectToServer();
        while (player.player_won == player.EMPTY) {
            if (player.my_color == player.player_turn) {
                player.requestBoardState();
                player.displayBoard();
                player.requestValidMoves();
                if (!player.skip_turn) {
                    player.debug_move();
                    player.checkForWin();
                } else {
                    player.player_turn = -player.my_color;
                }
            } else
                player.waitForTurn();
        }
        System.out.print("Game finished! You ");
        if (player.player_won == player.my_color)
            System.out.println(" win! Congratulations!");
        else
            System.out.println(" lost! Better luck next time!");
        player.closeConnection();
    }
}
