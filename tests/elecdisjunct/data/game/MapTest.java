package elecdisjunct.data.game;

import elecdisjunct.data.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Mia Fornes
 */

class MapTest {

    private Map instance;
    private Player[] players;
    private Node[] nodes;
    private Line[] lines;

    @BeforeEach
    void setUp() {

        instance = new Map();

        players = new Player[2];
        players[0] = new Player(100, "mia", Color.BUG);
        players[1] = new Player(20, "Dolan", Color.BUG);

        nodes = instance.getNodes();
        nodes[0].setOwner(players[0]);
        nodes[2].setOwner(players[1]);

        lines = instance.getLines();

    }

    @AfterEach
    void tearDown() {

        instance = null;
        players = null;
        nodes = null;
        lines = null;

    }

    @Test
    void testCanClaim() {

        System.out.println("Map: canClaim()");

        //players[0]
        assertFalse(instance.canClaim(nodes[13], players[0])); //no line between nodes[0] and nodes[13] yet
        assertTrue(instance.canClaim(lines[17], players[0]));

        //players[1]
        assertTrue(instance.canClaim(lines[59], players[1]));
        assertFalse(instance.canClaim(nodes[13], players[1])); //no node connected

    }

    @Test
    void testClaim() {

        System.out.println("Map: claim()");

        //players[0]
        assertTrue(instance.claim(lines[17], players[0]));
        assertTrue(instance.claim(nodes[13], players[0]));

        //players[1]
        assertTrue(instance.claim(lines[58], players[1]));
        assertTrue(instance.claim(nodes[33], players[1]));
        assertFalse(instance.claim(nodes[32], players[1]));
        assertFalse(instance.claim(lines[17], players[1])); //claimed by players[0]

    }

    @Test
    void testGetTotalOutput() {

        System.out.println("Map: getTotalOutput()");

        //players[0]
        assertEquals(0, instance.getTotalOutput(players[0])); //score = 0 at start
        instance.claim(lines[17], players[0]);
        instance.claim(nodes[13], players[0]);
        assertEquals(80, instance.getTotalOutput(players[0]));

        //players[1]
        assertEquals(0, instance.getTotalOutput(players[1])); //score = 0 at start
        instance.claim(lines[59], players[1]);
        instance.claim(nodes[32], players[1]);
        assertEquals(80, instance.getTotalOutput(players[1]));

    }

    @Test
    void testUpdate() {

        System.out.println("Map: update()");

        //Nodes
        assertEquals(1, nodes[4].getLevel());
        instance.claim(lines[0], players[0]);
        nodes[4].setLevel(2);
        instance.update(nodes[4]);
        assertEquals(2, nodes[4].getLevel());

        //Lines
        assertNull(lines[59].getOwner());
        instance.claim(lines[59], players[1]);
        instance.update(lines[59]);
        assertEquals(players[1], lines[59].getOwner());

    }

}