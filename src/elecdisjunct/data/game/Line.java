package elecdisjunct.data.game;

import com.sun.istack.internal.Nullable;

/**
 * Lines know which two nodes they connect, and cannot be upgraded at the moment.
 *
 * @author Tore Bergebakken
 */

public class Line extends MapComponent {

    private Node from;
    private Node to;

    //private static final byte MAX_LEVEL = 1;

    public Line(Node from, Node to) {

        if (from == null || to == null) throw new IllegalArgumentException("One endpoint is NULL");

        this.from = from;
        this.to = to;

    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    /**
     * We might decide to do the calculation in a more complex way that may render this method completely useless.
     * Currently it adds together the outputs of its two endpoints, diminishing either end if owned by somebody else, zeroing if unclaimed.
     *
     * @return  the power "passing through" (lol)
     */
    int getThroughput() {

        if (getOwner() == null) return to.getOutput() + from.getOutput();
        else return (from.getOwner() == null? 0 : (getOwner().equals(from.getOwner())? from.getOutput() : from.getOutput() / 2))
             + (to.getOwner() == null? 0 : (getOwner().equals(to.getOwner())? to.getOutput() : to.getOutput() / 2));
        //return (getOwner().equals(from.getOwner())? from.getOutput() : 0) + (getOwner().equals(to.getOwner())? to.getOutput() : 0);


    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    /**
     * A line is equal to another if both of its endpoints are equal.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false; // NO SUBCLASSES ACCEPTED

        Line line = (Line) o;

        return getFrom().equals(line.getFrom()) &&
                getTo().equals(line.getTo()); // this checks the coordinates.
    }

    @Override
    public String toString() {

        String NL = System.lineSeparator();

        return "From: " + from.getType() + NL +
                "To: " + to.getType() + NL +
                /*"Level: " + getLevel() + NL +*/
                (isClaimed()? "T" : "Potential T") + "hroughput: " + getThroughput();
    }

}
