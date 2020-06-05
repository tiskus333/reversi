package src.reversi;

import java.io.*;
import java.net.*;

public class Player {
    private final int EMPTY = 0;
    private final int WHITE = 1;
    private final int BLACK = -1;
    private final int DRAW = 2;
    private final int BOARD_SIZE = 8;
    public int player_turn = BLACK;
    public int my_color;
    private int player_won = EMPTY;;
    private boolean skip_turn = false;
    private static boolean new_game = true;
    private int points[];

    private int board[][];
    private int possible_moves[][];

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    /**
     * Create tables for storing game data
     */
    public Player() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        possible_moves = new int[BOARD_SIZE][BOARD_SIZE];
        points = new int[2];
    }

    public int getPlayer_turn() {
        return player_turn;
    }

    public void setPlayer_turn(int player_turn) {
        this.player_turn = player_turn;
    }

    public int getPlayer_won() {
        return player_won;
    }

    public int[] getPoints() {
        return points;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getMy_color() {
        return my_color;
    }

    public int[][] getPossible_moves() {
        return possible_moves;
    }

    public boolean isSkip_turn() {
        return skip_turn;
    }

    /**
     * Connect to the server at given ip
     * @param ip
     */
    public void connectToServer(String ip) {
        int tmp_id;
        System.out.println("Connecting to server at localhost:60065");
        try {
            socket = new Socket(ip, 60065);
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

    /**
     * Close socket
     */
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Cannot close connection.");
        }
    }

    /**
     * Initialize Board state
     */
    public void initBoard() {
        System.out.println("_____NEW GAME_____");
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
                board[i][j] = EMPTY;

        // board[3][3] = board[4][4] = WHITE;
        // board[3][4] = board[4][3] = BLACK;
        board[0][0] = board[0][6] = BLACK;
        board[0][1] = board[0][7] = WHITE;

        points[0] = 2;
        points[1] = 2;

        player_turn = BLACK;
        player_won = EMPTY;
    }

    /**
     * Request board from server, reads current positions and points
     */
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
            points[0] = dataIn.readInt();
            points[1] = dataIn.readInt();
            for (int i = 0; i < BOARD_SIZE; ++i)
                for (int j = 0; j < BOARD_SIZE; ++j) {
                    board[i][j] = dataIn.readInt();
                }
        } catch (IOException e) {
            System.out.println("IOException cannot read board state from server.");
        }
    }

    /**
     * Request possible moves for player, set skip_turn if no moves are possible
     */
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
        }
    }

    /**
     * Send move (x,y) to server
     * @param x
     * @param y
     */
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

    public void debug_move2(int[] move) {
        if (!skip_turn) {
            System.out.println(move[0] + " " + move[1]);
            System.out.println("sending");
            move(move[0], move[1]);
        }
    }

    /**
     * Wait for my turn signal from server, end game if recieved special values
     */
    public void waitForTurn() {
        try {
            player_turn = dataIn.readInt();
            if (player_turn == -my_color * 100) {
                player_won = -my_color;
                System.out.println("UPS i lost");
            }
            if (player_turn == DRAW * 100) {
                player_won = DRAW;
                System.out.println("DRAW");
            }
            if (player_turn == my_color * 100) {
                player_won = my_color;
                System.out.println("Yey, I won");
            }
        } catch (IOException e) {
            System.out.println("IOException, cannot connect to server.");
        }
    }

    /**
     * Request winner information from server
     * @return
     */
    public boolean checkForWin() {
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
        return player_won != EMPTY;
    }

    public boolean isMyTurn() {
        return my_color == player_turn;
    }

    /**
     * Send information about wanting to play again
     */
    private void newGame() {
        System.out.println("Checking if we play again");
        try {
            dataOut.writeBoolean(new_game);
            dataOut.flush();
            if (new_game)
                new_game = dataIn.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * main game loop
     */
    public static void game() {
        Player player = new Player();
        player.connectToServer("localhost");
        Window window = new Window(player.my_color, player.getPlayer_turn());
        while (new_game) {
            player.initBoard();
            window.displayBoard(player.getBoard(), player.getPoints());
            while (player.getPlayer_won() == player.EMPTY) {
                if (player.isMyTurn()) {
                    System.out.println("New turn");
                    player.requestBoardState();
                    window.displayBoard(player.getBoard(), player.getPoints());
                    player.requestValidMoves();
                    if (!player.skip_turn) {
                        window.displayPossibleMoves(player.getPossible_moves());
                        player.debug_move2(window.getMove());
                    }
                    if (player.checkForWin())
                        break;
                    System.out.println("NO WINNER");
                    player.requestBoardState();
                    window.displayBoard(player.getBoard(), player.getPoints());
                }
                player.waitForTurn();

            }
            System.out.println("game finished");
            player.requestBoardState();
            window.displayBoard(player.getBoard(), player.getPoints());
            new_game = window.displayWinner(player.getPlayer_won());
            player.newGame();
        }
        player.closeConnection();
    }

    public static void main(String[] args) {
        game();
        System.exit(0);
        return;
    }

}
