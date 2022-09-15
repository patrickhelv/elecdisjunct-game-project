package elecdisjunct.data.game;

import com.sun.istack.internal.Nullable;
import elecdisjunct.data.util.Color;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 *
 * @author Tore Bergebakken
 */

public class Player implements Comparable<Player> {

    private final int userID;
    private final String nickname;
    private Color color;
    private boolean present = true; // leaving sets it to false in the other clients

    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private int nodes = 0;
    private int lines = 0;

    public Player(int userID, String nickname, Color color) {
        this.userID = userID;
        this.nickname = nickname;
        this.color = color;
    }

    public Player(String nickname, int score, int nodes, int lines) {
        userID = 0;
        this.nickname = nickname;
        this.score.set(score);
        this.nodes = nodes;
        this.lines = lines;
    }


    public int getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) { this.color = color; }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }

    public int getScore() {
        return score.get();
    }

    void addToScore(int score) { this.score.set(getScore() + score); }

    public void setScore(int score) {
        this.score.set(score);
    }

    public boolean isPresent() {
        return present;
    }

    void setPresent(boolean present) {
        this.present = present;
    }

    public void leave() {
        present = false;
    }

    public int getNodes() {
        return nodes;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public void setNodes(int nodes){
        this.nodes = nodes;
    }

    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false; // NO SUBCLASSES ACCEPTED

        Player player = (Player) o;

        return getUserID() == player.getUserID();

    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(o.getScore(), getScore()); // TODO check if this DOES reverse the order and place the highest-scoring players at the top.
    }

    @Override
    public String toString() {
        //String NL = System.lineSeparator();
        return getNickname().concat(isPresent()? "" : " (has left)"); // remove score when done
    }
}
