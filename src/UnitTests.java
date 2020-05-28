package src;

import src.server.Server;
import src.reversi.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnitTests {

    Server server = new Server();

    @Test
    public void testEmptyBoardState() {
        Assert.assertNotNull(server.board);
        Assert.assertNotNull(server.possible_moves);
    }

    @Test
    public void testCheckValidMovesBlack() {
        server.initBoard();
        server.getValidPositions(1);
    }

    @Test
    public void testWinnerCheckWhite() {

    }

}