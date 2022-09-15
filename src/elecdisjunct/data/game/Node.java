package elecdisjunct.data.game;

import com.sun.istack.internal.Nullable;

/**
 * A node is either a city or a power plant.
 * They act differently but can be handled in the same way, <i>for the most part.</i>
 * A city has no type or classification (right now, TBD)
 *
 * @author Tore Bergebakken
 */
public abstract class Node extends MapComponent {

    private final int posX;
    private final int posY;

    public Node(int posX, int posY) { // level is 1 by default

        // TODO check if within bounds (using constants in Map or something)

        this.posX = posX;
        this.posY = posY;

    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    /**
     *
     *
     * @return  The produced power
     */
    public abstract int getOutput();

    public abstract String getType(); // moved here
    
    public abstract String getIcon();

    public abstract String getEnumName(); // very cheesy way of doing this (I want dem stats)

    /**
     * A node is equal to another if their coordinates are the same.
     * No overlap.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) return true;

        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return posX == node.getPosX() && posY == node.getPosY();
    }

}