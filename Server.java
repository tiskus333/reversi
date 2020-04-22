/**
 * Server
 */
public class Server {

    public static final int EMPTY = 0;
    public static final int WHITE = 1;
    public static final int BLACK = -1;
    public static final int BOARD_SIZE = 8;

    public static int players_turn = BLACK;
    public static int white_pieces_nr = 2;
    public static int black_pices_nr = 2;
    public static int player_won = 0;

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
    private static int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    public static int[][] getValidPositions(int player) {
        int[][] validPositions = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
                validPositions[i][j] = isValidPosition(i, j, player);
        return validPositions;
    }

    public static int isValidPosition(int x, int y, int player) {
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

    public static boolean checkPosition(int x, int y, int dx, int dy, int player, boolean possible) {
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

    public static void initBoard() {
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
                board[i][j] = EMPTY;

        board[3][3] = board[4][4] = WHITE;
        board[3][4] = board[4][3] = BLACK;
    }

    public static void move(int x, int y, int player) {
        int valid_positions[][] = getValidPositions(player);
        if (valid_positions[x][y] == 1) {
            board[x][y] = player;
            if (player == WHITE)
                ++white_pieces_nr;
            else
                ++black_pices_nr;
            updateBoard(x, y, player);
        } else
            System.out.println("INCORECT PLACEMNET!!!");
    }

    public static void updateBoard(int x, int y, int player) {
        update(x + 1, y, 1, 0, player); // ee
        update(x - 1, y, -1, 0, player); // ww
        update(x, y + 1, 0, 1, player); // nn
        update(x, y - 1, 0, -1, player); // ss
        update(x + 1, y + 1, 1, 1, player); // es
        update(x + 1, y - 1, 1, -1, player); // en
        update(x - 1, y + 1, -1, 1, player); // ws
        update(x - 1, y - 1, -1, -1, player); // wn
    }

    public static boolean update(int x, int y, int dx, int dy, int player) {
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

    public static void displayBoard() {
        System.out.println("black: " + black_pices_nr + " white: " + white_pieces_nr);
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", board[i][j]);
            }
            System.out.println();
        }

    }

    public static void displayPossiblePositions(int player) {
        int tab[][] = getValidPositions(player);
        System.out.print("VALID POSITIONS FOR ");
        if (player == 1)
            System.out.println("WHITE:");
        else
            System.out.println("BLACK:");
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                System.out.format("%3d", tab[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        initBoard();
        displayBoard();
        displayPossiblePositions(BLACK);
        move(2, 3, BLACK);
        displayBoard();
        displayPossiblePositions(WHITE);
        move(4, 2, WHITE);
        displayBoard();
        displayPossiblePositions(BLACK);
        move(0, 0, WHITE);
    }
}