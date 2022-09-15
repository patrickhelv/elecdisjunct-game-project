package elecdisjunct.data.game;

import elecdisjunct.data.util.Color;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A superclass standing as the outward abstraction between GUI code and the underlying game logic,<br/>
 * inherited by different classes to separate offline simulation (or possibly an interactive tutorial)<br/>
 * from whatever update procedure that is supposed to happen.<br/>
 * <br/>
 * Passes a bunch of method calls down to lower-level objects.<br/>
 * Many of the methods return booleans simply because it's easier for the subclasses to call super's method and know if it failed, and only then do their things.
 *
 * @author Tore Bergebakken
 */
public abstract class MatchAbstraction {

    private Match match;

    MatchAbstraction() {
        Player[] players = {new Player(1, "hey", Color.BUG), new Player(1337, "dolan", Color.RED)};
        match = new Match(33, players);
    }

    MatchAbstraction(int matchID, Player[] players) {
        match = new Match(matchID, players);
    }

    public abstract boolean isSimulated();

    public abstract boolean isTutorial();

    // ABSTRACTION SECTION - COMPONENTS

    // boolean for the sake of the subclass, must be overridden (like the other ones)

    /**
     * Simpler upgrade call, preventing the GUI from needing to pass a player object here
     */
    public boolean upgrade(MapComponent component) {
        if (match.getThisPlayer().equals(component.getOwner())) { // upgrade method checks if it can upgrade
            component.upgrade();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if this player can sabotage this component
     */
    public boolean canSabotage(MapComponent component) {
        //System.out.println(!component.isBroken()? "is not brok" : "is brok");
        //System.out.println(component.isClaimed()? "is claimd" : "is not claimd");
        //System.out.println(!match.getThisPlayer().equals(component.getOwner())? "is not own by yu" : "is own by u");

        return match.isSabotageAllowed() && !component.isBroken() && component.isClaimed() && !match.getThisPlayer().equals(component.getOwner()) && component instanceof PowerPlant;
    }

    /**
     * Actual implementation of sabotage method
     */
    public boolean sabotage(MapComponent component){
        if(canSabotage(component)){
            component.setBroken(true);
            match.setSabotaged(true);
            return true;
        }else {
            return false;
        }
    }

    // ABSTRACTION SECTION - MAP
    public Node[] getNodes() {
        return match.getMap().getNodes();
    }

    public Line[] getLines() {
        return match.getMap().getLines();
    }

    public boolean canClaim(MapComponent mapComponent) {
        return match.getMap().canClaim(mapComponent, match.getThisPlayer());
    }

    /**
     * Reroutes claim call, no need for GUI to fetch this player from match and so on
     */
    public boolean claim(MapComponent mapComponent) {
        return match.getMap().claim(mapComponent, match.getThisPlayer());
    }

    public int getTotalOutput() {
        return match.getMap().getTotalOutput(match.getThisPlayer());
    }

    // ABSTRACTION SECTION - MATCH
    public Match getMatch() { // yeah, let's let those beasts get raw access to our match
        return match;
    }

    // END TURN SECTION

    /**
     * In MatchUpdater this is called each time one of the action methods have been called<br/>
     * Ends a turn, incrementing counts accordingly, doing various checks and calling subroutines based on various factors
     */
    public void endTurn() {

        boolean wasAtYourTurn = match.isAtYourTurn(); // if your turn is the one that ends, remember that
        int lastRound = match.getRoundCount(); // remember what round you were at

        match.incrementTurnCount(); // go to next turn (and round)
        Logger.getLogger(MatchAbstraction.class.getName()).log(Level.INFO, "Ended turn, now at round " + (match.getRoundCount()+1) +
                                                    ", turn " + (match.getTurnCount()+1) + " active player: " + match.getActivePlayer().getNickname());

        if (wasAtYourTurn) {
            // FIRST CHECK FOR THIS TO ACTUALLY GET THAT SCORE INCREMENTED
            if (lastRound < match.getRoundCount()) { // if we went up a round
                endOfRoundRoutine();
            }

            // THEN YOU CAN START WHATEVER YOU NEED TO DO AFTER ENDING YOUR TURN
            if (match.getActivePlayer().isPresent()) {
                afterYourTurnRoutine(); // the normal stuff
            } else {
                nextPlayerNotPresentRoutine(); // the bizarre stuff
            }

            // NOTHING SHOULD HAPPEN AFTER THIS POINT, THE UPDATE THREAD HAS STARTED RUNNING! (if not simulated)

        }

    }

    /**
     * Solely for the purpose of giving players some more stats to look at while playing the game
     */
    public void updateComponentCounts() {
        for (Player player : match.getPlayers()) {
            player.setNodes(match.getMap().getTotalNodes(player));
            player.setLines(match.getMap().getTotalLines(player));
        }
    }

    public boolean leave() {
        match.getThisPlayer().leave();
        if (match.isAtYourTurn()) {
            match.incrementTurnCount();
        }
        return true;
    }

    /**
     * ONLY called in the client that actually has to take care of increasing the scores.<br/>
     * SHOULD be overridden and surrounded by other procedures in subclasses (at least if they do different sorts of updates)
     */
    void endOfRoundRoutine() {
        Logger.getLogger(MatchAbstraction.class.getName()).log(Level.INFO, "Incrementing scores...");
        for (Player player : match.getPlayers()) {
            player.addToScore(match.getMap().getTotalOutput(player));
        }
    }

    /**
     * Normally called when you're done with your turn.
     */
    abstract void afterYourTurnRoutine();

    /**
     * Runs instead of the method above if the next player isn't present
     */
    abstract void nextPlayerNotPresentRoutine();

}
