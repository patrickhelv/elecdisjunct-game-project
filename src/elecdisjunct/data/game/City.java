package elecdisjunct.data.game;

import java.util.Random;

/**
 * Cities don't produce power and cannot be claimed, simply enough.
 *
 * A randomly picked flavor text is displayed in the GUI.
 *
 * @author Tore Bergebakken
 */

public class City extends Node {

    City(int posX, int posY) {

        super(posX, posY);

    }

    // does not need to override the Property method

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    void setOwner(Player owner) {
        // does nothing
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public int getLevel() {
        return 3; // for the sake of visibility
    }

    @Override
    public boolean isClaimed() {
        return false;
    }

    @Override
    public int getOutput() {
        return 0;
    }

    @Override
    public String getType() {
        return "City";
    }

    @Override
    public String toString() {
        java.util.Random rng = new Random();
        int choice = rng.nextInt(69);

        if (choice < 10) {
            return "A buzzing beehive\nof wonderful people";
        } else if (choice < 15) {
            return "A depressing testimony\nto humanity's greatest failures";
        } else if (choice < 16) {
            return "A set of impossibly tall structures";
        } else if (choice < 17) {
            return "There is no such thing as a city,\na mysterious stranger tells you";
        } else {
            return "A bustling city";
        }
    }

    public String getIcon() {
        return "city.JPG";
    }

    @Override
    public String getEnumName() {
        return "city";
    }
}
