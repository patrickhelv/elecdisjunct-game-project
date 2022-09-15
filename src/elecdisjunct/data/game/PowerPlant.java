package elecdisjunct.data.game;

/**
 *
 *
 * @author Tore Bergebakken
 * @author Mia Fornes
 */

public abstract class PowerPlant extends Node {

    private static final byte MAX_LEVEL = 4;

    PowerPlant(int posX, int posY, int level) {

        super(posX, posY);

        if (level > MAX_LEVEL || level < 1) throw new IllegalArgumentException("Level not within range");

        setLevel(level);

    }

    @Override
    public boolean canUpgrade() {
        return getLevel() < MAX_LEVEL;
    }

    public abstract String getClassification();

    // moved getType to Node to allow the Line toString to get endpoint info

    @Override
    public String toString() {

        String NL = System.lineSeparator();

        return "Class: " + getClassification() + NL +
                "Type: " + getType() + NL +
                "Level: " + getLevel() + (getLevel() == MAX_LEVEL? " (max)" : "") + NL +
                "Output: " + getOutput() + (isBroken()? " (sabotaged)" : "");
    }
}

abstract class RenewablePowerPlant extends PowerPlant {

    private static final int[] OUTPUTS = {30, 120, 300, 600};

    RenewablePowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public int getOutput() {
        return isBroken() ? 0 : OUTPUTS[getLevel() - 1];
    }

    @Override
    public String getClassification() {
        return "Renewable";
    }
}

class WindmillPark extends RenewablePowerPlant {

    WindmillPark(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Windmill Park";
    }

    @Override
    public String getIcon() {
        return "windmill.JPG";
    }

    @Override
    public String getEnumName() {
        return "wind";
    }
}

class SolarPark extends RenewablePowerPlant {

    SolarPark(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Solar Park";
    }

    @Override
    public String getIcon() {
        return "solar.JPG";
    }

    @Override
    public String getEnumName() {
        return "solar";
    }
}

class HydroelectricPowerPlant extends RenewablePowerPlant {

    HydroelectricPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Hydroelectric Power";
    }

    @Override
    public String getIcon() {
        return "hydroelectric.JPG";
    }

    @Override
    public String getEnumName() {
        return "hydroelectric";
    }
}

abstract class NuclearPowerPlant extends PowerPlant {

    private static final int[] OUTPUTS = {300, 450, 600, 800};

    NuclearPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public int getOutput() {
        return isBroken() ? 0 : OUTPUTS[getLevel() - 1];
    }

    @Override
    public String getClassification() {
        return "Nuclear";
    }
}

class FusionReactor extends NuclearPowerPlant {

    FusionReactor(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Fusion Reactor";
    }

    @Override
    public String getIcon() {
        return "nuclear.JPG";
    }

    @Override
    public String getEnumName() {
        return "fusion";
    }
}

class FissionReactor extends NuclearPowerPlant {

    FissionReactor(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Fission Reactor";
    }

    @Override
    public String getIcon() {
        return "nuclear.JPG";
    }

    @Override
    public String getEnumName() {
        return "fission";
    }
}

abstract class FossilFuelPowerPlant extends PowerPlant {

    private static final int[] OUTPUTS = {50, 100, 250, 500};

    FossilFuelPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public int getOutput() {
        return isBroken() ? 0 : OUTPUTS[getLevel() - 1];
    }

    @Override
    public String getClassification() {
        return "Fossil Fuel";
    }
}

class CoalPowerPlant extends FossilFuelPowerPlant { //TODO find better name?

    CoalPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Coal Power Plant";
    }

    @Override
    public String getIcon() {
        return "coal.JPG";
    }

    @Override
    public String getEnumName() {
        return "coal";
    }
}

class GasPowerPlant extends FossilFuelPowerPlant { //find better name?

    GasPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public String getType() {
        return "Gas Power Plant";
    }

    @Override
    public String getIcon() {
        return "gas.JPG";
    }

    @Override
    public String getEnumName() {
        return "gas";
    }
}


