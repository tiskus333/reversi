import java.io.*;
import java.net.*;

/**
 * Server
 */
public class Server {

    public final int EMPTY = 0;
    public final int WHITE = 1;
    public final int BLACK = -1;
    public final int BOARD_SIZE = 8;

    public int player_turn = BLACK;
    public int white_pieces_nr = 2;
    public int black_pices_nr = 2;
    public int player_won;
    private int player_nr;
    private boolean is_possible_move;

    private ServerSocket ss;
    private Socket s;
    private ServerConnection player_black, player_white;
    /**
     *    INITIAL BOARD PLACEMENT
     *       0  1  2  3  4  5  6  7
     * 0     _  _  _  _  _  _  _  _
     * 1     _  _  _  _  _  _  _  _
     * 2     _  _  _  _  _  _  _  _
     * 3     _  _  _  W  B  _  _  _
     * 4     _  _  _  B  W  _  _  _
     * 5     _  _  _  _  _  _  _  _
     * 6     _  _  _  _  _  _  _  _
     * 7     _  _  _  _  _  _  _  _
     */
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] possible_moves = new int[BOARD_SIZE][BOARD_SIZE];

    public Server() {
        System.out.println("Started game server.");
        player_nr = 0;
        try {
            ss = new ServerSocket(60065);
        } catch (IOException e) {
            System.out.println("IOExcpetion from Server() constructor.");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for players to join.");
            while (player_nr < 2) {

                s = ss.accept();
                ++player_nr;
                System.out.println("Player #" + player_nr + " connceted.");
                ServerConnection sc = new ServerConnection(s, player_nr);
                if (player_nr == 1)
                    player_black = sc;
                else
                    player_white = sc;
            }
            System.out.println("Both players connected. No longer accepting new players.");
        } catch (IOException e) {
            System.out.println("IOExcpetion from acceptConnection().");
        }
    }

    public void closeConnection() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runGame() {
        Thread thread_black = new Thread(player_black);
        Thread thread_white = new Thread(player_white);
        thread_black.start();
        thread_white.start();
    }

    private class ServerConnection implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int player_id;

        public ServerConnection(Socket s, int id) {
            socket = s;
            if (id == 1)
                player_id = BLACK;
            else
                player_id = WHITE;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                dataOut.writeInt(id);
                dataOut.flush();
            } catch (Exception e) {
                System.out.println("IOException from ServerConnection() constructor.");
            }
        }

        private void sendBoardState() {
            // while (player_id != player_turn);
            System.out.println("Waiting for player to send board state to.");
            try {
                dataIn.readInt();
            } catch (Exception e) {
                System.out.println("Cannot connect to player #" + player_id);
            }

            try {
                System.out.println("Sending board to: " + player_turn);
                for (int i = 0; i < BOARD_SIZE; ++i)
                    for (int j = 0; j < BOARD_SIZE; ++j) {
                        dataOut.writeInt(board[i][j]);
                    }
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IOException, cannot send board to client#" + player_id);
            }
        }

        private void sendValidMoves() {
            //while (player_id != player_turn);
            System.out.println("Waiting for player to send possible moves to.");
            try {
                dataIn.readInt();
            } catch (Exception e) {
                System.out.println("Cannot connect to player #" + player_id);
            }
            try {
                System.out.println("Sending moves to: " + player_turn);
                for (int i = 0; i < BOARD_SIZE; ++i)
                    for (int j = 0; j < BOARD_SIZE; ++j) {
                        dataOut.writeInt(possible_moves[i][j]);
                    }
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IOException, cannot send possible moves to client #" + player_id);
            }
        }

        private void recieveMove() {
            try {
                int x = -1, y = -1;
                x = dataIn.readInt();
                y = dataIn.readInt();
                System.out.println("Recieved move " + x + ":" + y);
                move(x, y, player_id);
                //switchPlayers();
                displayBoard();
            } catch (IOException e) {
                System.out.println("Cannot read move from player #" + player_id);
            }
        }

        private void switchPlayers() {
            player_turn *= -1;

        }

        private void notifyEnemy() {
            try {
                switchPlayers();
                System.out.println("player " + (player_id) + " fnished");
                player_black.dataOut.writeInt(player_turn);
                player_black.dataOut.flush();
                player_white.dataOut.writeInt(player_turn);
                player_white.dataOut.flush();

            } catch (Exception e) {
                System.out.println("IOException, cannot notify players");
            }
        }

        private void waitForEnemy() {
            try {
                player_turn = dataIn.readInt();
            } catch (IOException e) {
                System.out.println("IOException, cannot read player turn");
            }
        }

        @Override
        public void run() {
            while (true) {
                // System.out.println(player_id + "" + player_turn);
                //if (player_id == player_turn) {
                System.out.println("From player #" + player_id + "Now plays: " + player_turn);
                sendBoardState();
                getValidPositions(player_id);
                if (is_possible_move) {
                    sendValidMoves();
                    recieveMove();
                }
                notifyEnemy();
                //} else
                //  waitForEnemy();
            }
        }

    }

    /**
     * 
     * 
     * 
     * 
     * 
     * @param player
     * @return
     */

    public int[][] getValidPositions(int player) {
        int tmp_move;
        is_possible_move = false;
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                tmp_move = isValidPosition(i, j, player);
                if (tmp_move == 1)
                    is_possible_move = true;
                possible_moves[i][j] = tmp_move;
            }
        return possible_moves;
    }

    public int isValidPosition(int x, int y, int player) {
        boolean isValid = false;

        if (board[x][y] != EMPTY)
            return 0;

        boolean direction[] = new boolean[8];
        direction[0] = checkPosition(x + 1, y, 1, 0, player, false); // ee
        direction[1] = checkPosition(x - 1, y, -1, 0, player, false); // ww
        direction[2] = checkPosition(x, y + 1, 0, 1, player, false); // nn
        direction[3] = checkPosition(x, y - 1, 0, -1, player, false); // ss
        direction[4] = checkPosition(x + 1, y + 1, 1, 1, player, false); // es
        direction[5] = checkPosition(x + 1, y - 1, 1, -1, player, false); // en
        direction[6] = checkPosition(x - 1, y + 1, -1, 1, player, false); // ws
        direction[7] = checkPosition(x - 1, y - 1, -1, -1, player, false); // wn

        for (int i = 0; i < direction.length; ++i) {
            isValid |= direction[i];
        }
        if (isValid)
            return 1;
        else
            return 0;
    }

    public boolean checkPosition(int x, int y, int dx, int dy, int player, boolean possible) {
        if (x >= BOARD_SIZE || x < 0 || y >= BOARD_SIZE || y < 0)
            return false;
        if (board[x][y] == EMPTY)
            return false;
        if (board[x][y] == player) {
            return possible;
        } else
            possible = true;
        return checkPosition(x + dx, y + dy, dx, dy, player, possible);
    }

    public void initBoard() {
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
                board[i][j] = EMPTY;

        board[3][3] = board[4][4] = WHITE;
        board[3][4] = board[4][3] = BLACK;
    }

    public void move(int x, int y, int player) {
        if (x < 0 || x > BOARD_SIZE || y < 0 || y > BOARD_SIZE)
            return;
        if (possible_moves[x][y] == 1) {
            board[x][y] = player;
            if (player == WHITE)
                ++white_pieces_nr;
            else
                ++black_pices_nr;
            updateBoard(x, y, player);
        } else
            System.out.println("INCORECT PLACEMENT!!!");
    }

    public void updateBoard(int x, int y, int player) {
        update(x + 1, y, 1, 0, player); // ee
        update(x - 1, y, -1, 0, player); // ww
        update(x, y + 1, 0, 1, player); // nn
        update(x, y - 1, 0, -1, player); // ss
        update(x + 1, y + 1, 1, 1, player); // es
        update(x + 1, y - 1, 1, -1, player); // en
        update(x - 1, y + 1, -1, 1, player); // ws
        update(x - 1, y - 1, -1, -1, player); // wn
    }

    public boolean update(int x, int y, int dx, int dy, int player) {
        if (x >= BOARD_SIZE || x < 0 || y >= BOARD_SIZE || y < 0)
            return false;
        if (board[x][y] == EMPTY)
            return false;
        if (board[x][y] == player)
            return true;
        if (update(x + dx, y + dy, dx, dy, player)) {
            board[x][y] = player;
            white_pieces_nr += player;
            black_pices_nr -= player;
            return true;
        }
        return false;
    }

    public void displayBoard() {
        System.out.println("black: " + black_pices_nr + " white: " + white_pieces_nr);
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", board[i][j]);
            }
            System.out.println();
        }
    }

    public void displayPossiblePositions(int player) {
        System.out.print("VALID POSITIONS FOR ");
        if (player == WHITE)
            System.out.println("WHITE:");
        else
            System.out.println("BLACK:");
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", possible_moves[i][j]);
            }
            System.out.println();
        }
    }

    public int[] getScore() {
        int score[] = new int[2];
        score[0] = white_pieces_nr;
        score[1] = black_pices_nr;
        return score;
    }

    public static void main(String[] args) {
        // Window window = new Window();
        // window.drawBoard();
        Server server = new Server();
        server.initBoard();
        server.acceptConnections();
        server.runGame();

        server.closeConnection();
        // displayBoard();
        // displayPossiblePositions(BLACK);
        // move(2, 3, BLACK);
        // displayBoard();
        // displayPossiblePositions(WHITE);
        // move(4, 2, WHITE);
        // displayBoard();
        // displayPossiblePositions(BLACK);
        // move(0, 0, WHITE);
    }
}