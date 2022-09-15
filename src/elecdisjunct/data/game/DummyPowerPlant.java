package elecdisjunct.data.game;

public class DummyPowerPlant extends PowerPlant {

    public DummyPowerPlant(int posX, int posY, int level) {
        super(posX, posY, level);
    }

    @Override
    public int getOutput() {
        return -1;
    }

    @Override
    public String getClassification() {
        return "Invalid Energy";
    }

    @Override
    public String getType() {
        return "THIS SHOULD NEVER BE SEEN";
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getEnumName() {
        return null; // BREAK IMMEDIATELY
    }
}
