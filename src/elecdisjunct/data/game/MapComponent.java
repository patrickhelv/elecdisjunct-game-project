package elecdisjunct.data.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Common methods for lines and nodes,
 * prevents having duplicate code in update methods and the GUI controller
 *
 *
 * @author Tore Bergebakken
 */
public abstract class MapComponent {

    private SimpleObjectProperty<Player> owner = new SimpleObjectProperty<>(null);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(1);
    private boolean broken = false;


    // OWNER SECTION

    public SimpleObjectProperty<Player> ownerProperty() {
        return owner;
    }

    public Player getOwner() {
        return owner.getValue();
    }

    /**
     * Will simply assign the new player as the owner
     * (this is a package-private method, after all)
     *
     * @param owner    the new owner
     */
    void setOwner(Player owner) {
        this.owner.setValue(owner);
    }

    public boolean isClaimed() {
        return owner.getValue() != null;
    }


    // LEVEL SECTION

    public SimpleIntegerProperty levelProperty() {
        return level;
    }

    public int getLevel() {
        return level.getValue();
    }

    void setLevel(int level) {
        if (level > 0) this.level.set(level);
    }

    public abstract boolean canUpgrade(); // the only abstract one

    boolean upgrade() {
        if (canUpgrade()) {
            level.set(getLevel() + 1);
            return true;
        } else {
            return false;
        }
    }


    // SABOTAGE SECTION

    public boolean isBroken() {
        return broken;
    }

    void setBroken(boolean broken) {
        this.broken = broken;
    }

}