package elecdisjunct.data.util;

import javafx.scene.paint.Paint;

import java.util.ArrayList;

/**
 * Simple enum for keeping track of our colors
 * Constructed with a hexadecimal code, has a getPaint() method for JavaFX purposes
 *
 * Use Color.valueOf(someString.toUpperCase()) to get the corresponding color (typos are fatal)
 *
 * @author Tore Bergebakken
 */
public enum Color {
    RED     ("#D8402F"),
            // "#e5273a"),
    /*#ff0000*/
    ORANGE  ("#FF8726"),
    // "#fc9928"),
    /*#ff0f00*/
    YELLOW  ("#E0B123"),//"#fcea28"),
    /*#ffff00*/
    GREEN   ("#55992D"),
    //#00ff00
    BLUE    ("#0074BC"), //#007fce"),
    //#0000ff
    PURPLE  ("#9A47A5"),
    //#ff00ff  #a112c9
    NEUTRAL ("#000000"),
    SELECTED("#427793"),
    NODE_BG ("#ffffff"),
    BUG     ("#555555"); // should never show up in the GUI

    private final String hexcode;
    private Paint paint;

    Color(String hexcode) {
        this.hexcode = hexcode;
        paint = Paint.valueOf(hexcode);
    }

    public String getHexcode() {
        return hexcode;
    }

    public Paint getPaint() {
        return paint;
    }

    private static ArrayList<Color> getColorChoices() {
        ArrayList<Color> unclaimed = new ArrayList<>();
        Color[] values = Color.values();
        for (Color color : values) {
            unclaimed.add(color);
        }
        unclaimed.remove(Color.BUG);
        unclaimed.remove(Color.NODE_BG);
        unclaimed.remove(Color.NEUTRAL);
        unclaimed.remove(Color.SELECTED);
        return unclaimed;
    }

    public static ArrayList<Color> unclaimedColors(ArrayList<Color> claimed) {

        ArrayList<Color> unclaimed = getColorChoices();

        for (Color color : claimed) {
            unclaimed.remove(color);
        }
        return unclaimed;
    }



}
