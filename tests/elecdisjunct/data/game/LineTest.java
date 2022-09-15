package elecdisjunct.data.game;

import elecdisjunct.data.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Victoria Blichfeldt
 * @author Tore Bergebakken
 */
class LineTest {

    private static final Player YOU = new Player(1337, "Dolan", Color.BUG);
    private static final Player OTHER = new Player(420, "Todd", Color.BUG);
    private Line instance;

    @BeforeEach
    void setUp() {
        FissionReactor from = new FissionReactor(23, 23, 4); // 800
        WindmillPark to = new WindmillPark(24, 24, 3); // 300
        instance = new Line(from, to);
        instance.setOwner(YOU);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isClaimed() {
        System.out.println("Line: isClaimed()");
        Map map = new Map();
        map.claim(instance, YOU);
        assertTrue(instance.isClaimed());
    }

    @Test
    void upgrade() {
        System.out.println("Line: upgrade");
        instance.upgrade();
        assertFalse(instance.getLevel() == 2);
        //assertEquals(2, instance.getLevel());
    }

    @Test
    void isBroken() {
        //TODO - do if sabotage is implemented
    }

    @Test
    void testGetFrom() {
        System.out.println("Line: getFrom()");
        assertEquals(new FissionReactor(23,23,4),instance.getFrom());
    }

    @Test
    void testGetTo() {
        System.out.println("Line: getTo()");
        assertEquals(new WindmillPark(24,24,3), instance.getTo());
    }

    @Test
    void testGetThroughput() {
        System.out.println("Line: getThroughput()");
        assertEquals(0, instance.getThroughput()); // none owned
        instance.getFrom().setOwner(YOU);
        assertEquals(800, instance.getThroughput()); // other end unclaimed
        instance.getTo().setOwner(YOU);
        assertEquals(1100, instance.getThroughput()); // both owned by you
        instance.getFrom().setOwner(OTHER);
        assertEquals(700, instance.getThroughput()); // 800 / 2 + 300
    }

    @Test
    void testCanUpgrade() {
        System.out.println("Line: canUpgrade");
        assertFalse(instance.canUpgrade());
    }

    @Test
    void testEquals() {
        System.out.println("Line: equals()");
        FissionReactor from = new FissionReactor(23, 23, 4); // 800
        WindmillPark to = new WindmillPark(24, 24, 3); // 300
        Line line = new Line(from, to);
        assertEquals(instance, line);
    }
}