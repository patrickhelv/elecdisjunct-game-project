package elecdisjunct.data.game;

import elecdisjunct.data.user.User;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.data.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static elecdisjunct.data.game.Match.ROUND_LIMIT;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Mia Fornes
 */

class MatchTest {

    private Match instance;
    private Player[] players;
    private User user;
    private Node[] nodes;
    private Line[] lines;
    private Map map;


    @BeforeEach
    void setUp() {

        map = new Map();

        players = new Player[2];
        user = new User(100, "mia@test.com", "mia");
        UserHandler.setUser(user);
        players[0] = new Player(100, "mia", Color.BUG);
        players[1] = new Player(20, "Dolan", Color.BUG);

        nodes = new Node[4];
        nodes[0] = new FissionReactor(23, 23, 1);
        nodes[1] = new WindmillPark(24, 24, 1);
        nodes[2] = new SolarPark(25, 25, 1);
        nodes[3] = new City(26, 26);

        lines = new Line[2];
        lines[0] = new Line(nodes[0], nodes[1]);
        lines[1] = new Line(nodes[1], nodes[2]);

        instance = new Match(100, players);

    }

    @AfterEach
    void tearDown() {

        instance = null;
        players = null;
        nodes = null;
        lines = null;

    }

    @Test
    void testGetMatchID() {

        System.out.println("Match: getMatchID()");
        assertEquals(100, instance.getMatchID());
    }

    @Test
    void testGetRoundCount() {

        System.out.println("Match: getRoundCount()");
        assertEquals(0, instance.getRoundCount());

    }

    @Test
    void testSetAndGetRoundCount() {

        System.out.println("Match: setRoundCount(), getRoundCount()");
        instance.setRoundCount(2);
        assertEquals(2, instance.getRoundCount());

    }

    @Test
    void testSetAndGetTurnCount() {

        System.out.println("Match: setTurnCount(), getTurnCount()");
        instance.setTurnCount(2);
        assertEquals(2, instance.getTurnCount());

    }

    @Test
    void testGetPlayers() {

        System.out.println("Match: getPlayers()");
        assertEquals(players, instance.getPlayers());

    }

    @Test
    void testGetRankedPlayers() {

        System.out.println("Match: getRankedPlayers()");
        players[0].setScore(10000);
        players[1].setScore(20000);
        Player[] sorted = {players[1], players[0]}; //players[1] has the highest score
        assertArrayEquals(sorted, instance.getRankedPlayers());

    }

    @Test
    void testGetThisPlayer() {

        System.out.println("Match: getThisPlayer()");
        assertEquals(players[0], instance.getThisPlayer());

    }

    @Test
    void testGetActivePlayer() {

        System.out.println("Match: getActivePlayer()");
        assertEquals(players[0], instance.getActivePlayer());
        instance.incrementTurnCount();
        assertEquals(players[1], instance.getActivePlayer());

    }

    @Test
    void testIncrementTurnCount() {

        System.out.println("Match: incrementTurnCount()");
        instance.incrementTurnCount();
        assertEquals(1, instance.getTurnCount());
        instance.incrementTurnCount();
        assertEquals(0, instance.getTurnCount());
        assertEquals(1, instance.getRoundCount()); //players both took their turn -> new round

    }

    @Test
    void testIsAtYourTurn() {

        System.out.println("Match: isAtYourTurn()");
        assertTrue(instance.isAtYourTurn());
        instance.incrementTurnCount();
        assertFalse(instance.isAtYourTurn()); //next player's turn
        instance.incrementTurnCount();
        assertTrue(instance.isAtYourTurn());

    }

    @Test
    void testIsAtEnd() {

        System.out.println("Match: isAtEnd()");
        for(int i = 0; i < ROUND_LIMIT * 2; i++) {
            instance.incrementTurnCount();
        }
        assertTrue(instance.isAtEnd());

    }

}