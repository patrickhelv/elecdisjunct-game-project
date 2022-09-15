package elecdisjunct.data.game;

import elecdisjunct.data.util.Color;

/**
 * Simple factory class to use when fetching updates from DB
 *
 * @author Tore Bergebakken
 */
public class DummyFactory {

    public static Player createDummyPlayer(int userID, int score, boolean present) { // for later, should replace the one below sometime
        Player player = createDummyPlayer(userID, score);
        player.setPresent(present);
        return player;
    }

    public static Player createDummyPlayer(int userID, int score) {
        Player player = createDummyPlayer(userID);
        player.setScore(score);
        return player;
    }

    private static Player createDummyPlayer(int userID) {
        return new Player(userID, "Do Not Knows", Color.BUG);
    }

    public static Player createLobbyPlayer(int userID, String nickname, String color) {
        return new Player(userID, nickname, Color.valueOf(color.toUpperCase()));
    }

    public static Node createDummyNode(int posX, int posY, int level, boolean broken, int ownerID) {
        DummyPowerPlant res = new DummyPowerPlant(posX, posY, level);
        res.setBroken(broken);
        res.setOwner(createDummyPlayer(ownerID));
        return res;
    }

    public static Line createDummyLine(int fromX, int fromY, int toX, int toY, int level, boolean broken, int ownerID) {
        City from = new City(fromX, fromY);
        City to = new City(toX, toY);
        Line line = new Line(from, to);
        line.setLevel(level);
        line.setBroken(broken);
        line.setOwner(createDummyPlayer(ownerID));
        return line;
    }

}
