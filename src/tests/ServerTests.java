package src.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import src.server.Server;

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
                Assert.assertNotNull(server);
        }

        @After
        public void cleanup() {
                server.closeConnection();
        }

        @Test
        public void testCheckValidMoves() {
                int[][] expected_board_white;
                int[][] expected_board_black;
                int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
//@formatter:off

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
        
        expected_board_white = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 1, 0, 0 }, 
                { 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
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
        public void testMove() {
                int[][] expected_board_white;
                int[][] expected_board_black;
                int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
//@formatter:off
        server.getValidPositions(WHITE);
        server.move(3, 5, WHITE);
        expected_board_white = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 1, 1, 1, 0, 0 }, 
                { 0, 0, 0,-1, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        board = server.getBoard();
        Assert.assertArrayEquals(expected_board_white, board);
server.initBoard();

        server.getValidPositions(BLACK);
        server.move(3, 2, BLACK);
        expected_board_black = new int[][] { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0,-1,-1,-1, 0, 0, 0 }, 
                { 0, 0, 0,-1, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 0, 0, 0, 0, 0 } };
        board = server.getBoard();
        Assert.assertArrayEquals(expected_board_black, board);
//@formatter:on
        }

        @Test
        public void testWinner() {
        //@formatter:off
        int player_won;
                int[][] draw_board_full = new int[][]{ 
                        { 1, 1, 1, 1,-1,-1,-1,-1 }, 
                        { 1, 1, 1, 1,-1,-1,-1,-1 },
                        { 1, 1, 1, 1,-1,-1,-1,-1 }, 
                        { 1, 1, 1, 1,-1,-1,-1,-1 }, 
                        { 1, 1, 1, 1,-1,-1,-1,-1 },
                        { 1, 1, 1, 1,-1,-1,-1,-1 }, 
                        { 1, 1, 1, 1,-1,-1,-1,-1 }, 
                        { 1, 1, 1, 1,-1,-1,-1,-1 } };
                server.initBoard();
                server.setBoard(draw_board_full);
                player_won =  server.checkWinner();
                Assert.assertEquals(DRAW, player_won);

                int[][] draw_board_partial = new int[][]{ 
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0,-1,-1, 0, 0, 0 }, 
                        { 0, 0, 0,-1,-1, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 1, 1 }, 
                        { 0, 0, 0, 0, 0, 0, 1, 1 } };
                server.initBoard();
                server.setBoard(draw_board_partial);
                server.getValidPositions(WHITE);
                server.getValidPositions(BLACK);
                player_won =  server.checkWinner();
                Assert.assertEquals(DRAW, player_won);

                int[][] win_board_white_full = new int[BOARD_SIZE][BOARD_SIZE];
                for (int[] is : win_board_white_full) {
                        for (int is2 : is) {
                                is2 = WHITE;
                        }
                }
                server.initBoard();
                server.setBoard(win_board_white_full);
                player_won =  server.checkWinner();
                Assert.assertEquals(WHITE, player_won);

                int[][] win_board_white_partial = new int[][] { 
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 1, 1, 0, 0, 0 }, 
                        { 0, 0, 0, 1, 1, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 } };
                server.initBoard();
                server.setBoard(win_board_white_partial);
                player_won =  server.checkWinner();
                Assert.assertEquals(WHITE, player_won);
                        
                int[][] win_board_black_full = new int[BOARD_SIZE][BOARD_SIZE];
                for (int[] is : win_board_black_full) {
                        for (int is2 : is) {
                                is2 = BLACK;
                        }
                }
                
                int[][] win_board_black_partial = new int[][] { 
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0,-1,-1, 0, 0, 0 }, 
                        { 0, 0, 0,-1,-1, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                        { 0, 0, 0, 0, 0, 0, 0, 0 } };
                server.initBoard();
                server.setBoard(win_board_black_partial);
                player_won =  server.checkWinner();
                Assert.assertEquals(BLACK, player_won);

        //@formatter:on
        }

}