package src.tests;

import src.server.Server;
import src.reversi.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTests {
    private final int EMPTY = 0;
    private final int WHITE = 1;
    private final int BLACK = -1;
    private final int DRAW = 2;
    private final int BOARD_SIZE = 8;
    Server server;

    @Before
    public void setup() {
        server = new Server();
        server.initBoard();
    }

    @Test
    public void testEmptyBoardState() {
        Assert.assertNotNull(server);
    }

    @Test
    public void testCheckValidMoves() {
        int[][] expected_board_white;
        int[][] expected_board_black;
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
//@formatter:off
        expected_board_white = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 1, 0, 0 }, 
                { 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        expected_board_black = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0, 0, 0, 0 }, 
                { 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };

                
        board = server.getValidPositions(BLACK);
        Assert.assertArrayEquals(expected_board_black, board);
        
        board = server.getValidPositions(WHITE);
        Assert.assertArrayEquals(expected_board_white, board);

        server.move(3, 5, WHITE);
        expected_board_white = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        board = server.getValidPositions(WHITE);
        Assert.assertArrayEquals(expected_board_white, board);

        server.move(5, 3, WHITE);
        expected_board_white = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        board = server.getValidPositions(WHITE);
        Assert.assertArrayEquals(expected_board_white, board);
            
        expected_board_black = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        board = server.getValidPositions(BLACK);
        Assert.assertArrayEquals(expected_board_black, board);
//@formatter:on
    }

    @Test
    public void testWinner() {

    }

}