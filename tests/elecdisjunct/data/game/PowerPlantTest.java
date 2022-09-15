package elecdisjunct.data.game;

import elecdisjunct.data.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Mia Fornes
 * @author Tore Bergebakken
 */

class PowerPlantTest {

    private Node[] instances;
    private static final Player owner = new Player(100, "Mia", Color.BUG);
    private static final Player newOwner = new Player(110, "Ola", Color.BUG);


    @BeforeEach
    void setUp() {

        instances = new Node[4];
        instances[0] = new City(23, 23);
        instances[1] = new CoalPowerPlant(24, 24, 3);
        instances[2] = new FissionReactor(25, 25, 2);
        instances[3] = new SolarPark(26, 26, 4);

        instances[1].setOwner(owner);
        instances[2].setOwner(owner);
        instances[3].setOwner(owner);

    }

    @AfterEach
    void tearDown() {
        instances = null;
    }

    @Test
    void testGetPosX() {

        System.out.println("PowerPlant: getPosX()");
        assertEquals(23, instances[0].getPosX());
        assertEquals(24, instances[1].getPosX());
        assertEquals(25, instances[2].getPosX());
        assertEquals(26, instances[3].getPosX());

    }

    @Test
    void testGetPosY() {

        System.out.println("PowerPlant: getPosY()");
        assertEquals(23, instances[0].getPosY());
        assertEquals(24, instances[1].getPosY());
        assertEquals(25, instances[2].getPosY());
        assertEquals(26, instances[3].getPosY());

    }

    @Test
    void testEquals() {

        System.out.println("PowerPlant: equals()");
        Node n1 = new WindmillPark(23, 23, 1);
        Node n2 = new HydroelectricPowerPlant(24, 24, 3);
        Node n3 = new City(25, 25);
        Node n4 = new FusionReactor(26, 26, 4);

        assertEquals(instances[0], n1);
        assertEquals(instances[1], n2);
        assertEquals(instances[2], n3);
        assertEquals(instances[3], n4);

    }

    @Test
    void testSetAndGetOwner() {

        System.out.println("PowerPlant: getOwner(), setOwner()");
        instances[1].setOwner(newOwner);
        assertEquals(newOwner, instances[1].getOwner());
        instances[2].setOwner(newOwner);
        assertEquals(newOwner, instances[2].getOwner());
        instances[3].setOwner(newOwner);
        assertEquals(newOwner, instances[3].getOwner());

    }

    @Test
    void testIsClaimed() {

        System.out.println("PowerPlant: isClaimed()");
        Map map = new Map();

        map.claim(instances[0], owner);
        map.claim(instances[1], owner);
        map.claim(instances[2], owner);
        map.claim(instances[3], owner);

        assertFalse(instances[0].isClaimed());//cannot claim City
        assertTrue(instances[1].isClaimed());
        assertTrue(instances[2].isClaimed());
        assertTrue(instances[3].isClaimed());

    }

    @Test
    void testGetType() {

        System.out.println("PowerPlant: getType()");
        assertEquals("City", instances[0].getType());
        assertEquals("Coal Power Plant", instances[1].getType());
        assertEquals("Fission Reactor", instances[2].getType());
        assertEquals("Solar Park", instances[3].getType());

    }

    @Test
    void testGetIcon() {

        System.out.println("PowerPlant: getIcon()");
        assertEquals("city.JPG", instances[0].getIcon());
        assertEquals("coal.JPG", instances[1].getIcon());
        assertEquals("nuclear.JPG", instances[2].getIcon());
        assertEquals("solar.JPG", instances[3].getIcon());

    }

    @Test
    void testGetEnumName() {

        System.out.println("PowerPlant: getEnumName()");
        assertEquals("city", instances[0].getEnumName());
        assertEquals("coal", instances[1].getEnumName());
        assertEquals("fission", instances[2].getEnumName());
        assertEquals("solar", instances[3].getEnumName());

    }

    @Test
    void testUpgrade() {

        System.out.println("PowerPlant: upgrade()");
        assertEquals(3, instances[0].getLevel());
        instances[0].upgrade(); // no difference
        assertEquals(3, instances[0].getLevel());
        instances[1].upgrade(); // ++
        instances[1].upgrade(); // then no difference
        assertEquals(4, instances[1].getLevel());
        instances[2].upgrade(); // ++
        assertEquals(3, instances[2].getLevel());
        instances[3].upgrade(); // no difference
        assertEquals(4, instances[3].getLevel());

    }

    @Test
    void testCanUpgrade() {

        System.out.println("PowerPlant: canUpgrade()");
        assertFalse(instances[0].canUpgrade());
        assertTrue(instances[1].canUpgrade());
        assertFalse(instances[3].canUpgrade());

    }

    @Test
    void testIsAndSetBroken() {

        System.out.println("PowerPlant: isBroken(), setBroken()");
        assertFalse(instances[0].isBroken());
        instances[0].setBroken(true);
        assertTrue(instances[0].isBroken());

    }

    @Test
    void testGetOutput() { // TODO these values *will* have to be tweaked, resulting in the test failing.

        System.out.println("PowerPlant: getOutput()");
        assertEquals(0, instances[0].getOutput());
        assertEquals(250, instances[1].getOutput());
        assertEquals(450, instances[2].getOutput());
        assertEquals(600, instances[3].getOutput());

    }

}