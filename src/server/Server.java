package src.server;

import java.io.*;
import java.net.*;

/**
 * Server
 */
public class Server {

    public final int EMPTY = 0;
    public final int WHITE = 1;
    public final int BLACK = -1;
    private final int DRAW = 2;
    public final int BOARD_SIZE = 8;

    public int player_turn = BLACK;
    public int white_pieces_nr;
    public int black_pices_nr;
    public int player_won;
    private int player_nr;
    private boolean is_possible_move;
    private boolean repeat_game = true;

    private ServerSocket ss;
    private Socket s;
    private ServerConnection player_black, player_white;

    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] possible_moves = new int[BOARD_SIZE][BOARD_SIZE];

    public Server() {
        System.out.println("Started game server.");
        player_nr = 0;
        player_won = EMPTY;
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

    public boolean repeatGame() {
        return player_white.new_game == true == player_black.new_game;
    }

    public void newGame() {
        repeat_game = false;
        try {
            player_black.new_game = player_black.dataIn.readBoolean();
            player_white.new_game = player_white.dataIn.readBoolean();
            repeat_game = ((player_white.new_game == true) && (true == player_black.new_game));
            player_black.dataOut.writeBoolean(repeat_game);
            player_black.dataOut.flush();
            player_white.dataOut.writeBoolean(repeat_game);
            player_white.dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runGame() {
        Thread game = new Thread(player_black);
        game.start();
        try {
            game.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ServerConnection implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int player_color;
        private boolean skip_turn;
        private boolean new_game = true;

        public ServerConnection(Socket s, int id) {
            socket = s;
            if (id == 1)
                player_color = BLACK;
            else
                player_color = WHITE;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                dataOut.writeInt(id);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IOException from ServerConnection() constructor.");
            }
        }

        private void sendBoardState() {
            System.out.println("Waiting for player to send board state to.");
            try {
                dataIn.readInt();
            } catch (IOException e) {
                System.out.println("Cannot connect to player #" + player_color);
            }

            try {
                System.out.println("Sending board to: " + player_color);
                dataOut.writeInt(black_pices_nr);
                dataOut.writeInt(white_pieces_nr);
                for (int i = 0; i < BOARD_SIZE; ++i)
                    for (int j = 0; j < BOARD_SIZE; ++j) {
                        dataOut.writeInt(board[i][j]);
                    }
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IOException, cannot send board to client#" + player_color);
                player_won = -player_color;
            }
        }

        private void sendValidMoves() {
            System.out.println("Waiting for player to send possible moves to.");
            try {
                dataIn.readInt();
            } catch (IOException e) {
                System.out.println("Cannot connect to player #" + player_color);
            }
            getValidPositions(player_color);
            skip_turn = !is_possible_move;
            try {
                dataOut.writeBoolean(skip_turn);
                dataOut.flush();
                if (skip_turn) {
                    System.out.println("Skipping player #" + player_color + " turn");
                    return;
                }
                System.out.println("Sending moves to: " + player_turn);
                for (int i = 0; i < BOARD_SIZE; ++i)
                    for (int j = 0; j < BOARD_SIZE; ++j) {
                        dataOut.writeInt(possible_moves[i][j]);
                    }
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IOException, cannot send possible moves to client #" + player_color);
                player_won = -player_color;
            }
        }

        private void recieveMove() {
            try {
                int x = -1, y = -1;
                x = dataIn.readInt();
                y = dataIn.readInt();
                System.out.println("Recieved move " + x + ":" + y);
                move(x, y, player_color);
                displayBoard();
            } catch (IOException e) {
                System.out.println("Cannot read move from player #" + player_color);
                player_won = -player_color;
            }
        }

        private boolean checkForWinner() {
            System.out.println("Waiting for player to send winner to.");
            try {
                dataIn.readInt();
            } catch (IOException e) {
                System.out.println("Cannot connect to player #" + player_color);
                player_won = -player_color;
            }
            if (white_pieces_nr == (BOARD_SIZE * BOARD_SIZE) || black_pices_nr == 0 || player_won == WHITE) {
                player_won = WHITE;
            } else if (black_pices_nr == (BOARD_SIZE * BOARD_SIZE) || white_pieces_nr == 0 || player_won == BLACK) {
                player_won = BLACK;
            } else if ((black_pices_nr + white_pieces_nr) == (BOARD_SIZE * BOARD_SIZE)) {
                if (black_pices_nr < white_pieces_nr)
                    player_won = WHITE;
                if (black_pices_nr > white_pieces_nr)
                    player_won = BLACK;
                if (black_pices_nr == white_pieces_nr)
                    player_won = DRAW;
            } else if (player_black.skip_turn && player_white.skip_turn) {
                if (black_pices_nr < white_pieces_nr)
                    player_won = WHITE;
                if (black_pices_nr > white_pieces_nr)
                    player_won = BLACK;
                if (black_pices_nr == white_pieces_nr)
                    player_won = DRAW;
            }
            try {
                dataOut.writeInt(player_won);
                dataOut.flush();

            } catch (IOException e) {
                System.out.println("Cannot connect to player #-1");
            }
            if (player_won == BLACK)
                System.out.println("Player BLACK wins!");
            else if (player_won == WHITE)
                System.out.println("Player WHITE wins!");
            else if (player_won == DRAW)
                System.out.println("It's a DRAW!");

            return player_won != EMPTY;
        }

        private void endGame() {
            new_game = false;
            try {
                if (player_won == BLACK) {
                    if (player_turn == WHITE) {
                        player_black.dataOut.writeInt(BLACK * 100);
                        player_black.dataOut.flush();
                    }
                    if (player_turn == BLACK) {
                        player_white.dataOut.writeInt(BLACK * 100);
                        player_white.dataOut.flush();
                    }

                } else if (player_won == WHITE) {
                    if (player_turn == BLACK) {
                        player_white.dataOut.writeInt(WHITE * 100);
                        player_white.dataOut.flush();
                    }
                    if (player_turn == WHITE) {
                        player_black.dataOut.writeInt(WHITE * 100);
                        player_black.dataOut.flush();
                    }
                } else if (player_won == DRAW) {
                    if (player_turn == BLACK) {
                        player_white.dataOut.writeInt(DRAW * 100);
                        player_white.dataOut.flush();
                    }
                    if (player_turn == WHITE) {
                        player_black.dataOut.writeInt(DRAW * 100);
                        player_black.dataOut.flush();
                    }
                }
            } catch (IOException e) {
            }
        }

        public void nextPlayer(int nextPlayer) {

            try {
                player_turn = nextPlayer;
                System.out.println("NOW PLAYING: #" + nextPlayer);
                player_black.dataOut.writeInt(nextPlayer);
                player_white.dataOut.writeInt(nextPlayer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (repeat_game) {
                initBoard();
                while (player_won == EMPTY) {
                    player_black.sendBoardState();
                    player_black.sendValidMoves();
                    if (!player_black.skip_turn)
                        player_black.recieveMove();
                    if (player_black.checkForWinner())
                        break;
                    player_black.sendBoardState();
                    nextPlayer(WHITE);
                    player_white.sendBoardState();
                    player_white.sendValidMoves();
                    if (!player_white.skip_turn)
                        player_white.recieveMove();
                    if (player_white.checkForWinner())
                        break;
                    player_white.sendBoardState();
                    nextPlayer(BLACK);

                }
                endGame();
                player_black.sendBoardState();
                player_white.sendBoardState();
                newGame();
            }
        }

    }

    public int[][] getValidPositions(int player) {
        int tmp_move;
        is_possible_move = false;
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
                possible_moves[i][j] = 0;

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
        if (possible_moves[x][y] == 1)
            return 1;

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
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                board[i][j] = EMPTY;
                possible_moves[i][j] = EMPTY;
            }
        player_turn = BLACK;
        player_won = EMPTY;
        //new_game = false;
        System.out.println("______NEW GAME______");
        board[3][3] = board[4][4] = WHITE;
        board[3][4] = board[4][3] = BLACK;
        // board[0][0] = board[0][6] = BLACK;
        // board[0][1] = board[0][7] = WHITE;
        black_pices_nr = 2;
        white_pieces_nr = 2;
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

    public static void main(String[] args) {
        Server server = new Server();
        server.initBoard();
        server.acceptConnections();
        server.runGame();
        server.closeConnection();
    }
}