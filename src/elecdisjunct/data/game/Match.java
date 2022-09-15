package elecdisjunct.data.game;

import elecdisjunct.data.user.UserHandler;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;

/**
 * Keeps track of round and turn count, holds player list and map instance.
 *
 * @author Tore Bergebakken
 */
public class Match {

    private final int matchID;

    private Player[] players;
    private int numPlayers;
    private Player thisPlayer;
    private int activeTurn = -1;

    private Map map;

    public static final int ROUND_LIMIT = 26;
    private SimpleIntegerProperty roundCount = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty turnCount = new SimpleIntegerProperty(0);

    public static final int SABOTAGE_START = 17;
    private boolean sabotaged = false;

    /**
     * Creates a new match object given a list of players,<br/>
     * initializes the local map instance and finds the player that is controlling this client.
     *
     * @param matchID   This match's ID in DB (or just a bogus number)
     * @param players   The players in the match
     */
    Match(int matchID, Player[] players) {

        if (players.length < 2 || players.length > 4) throw new IllegalArgumentException("2-4 players only");

        for (Player player : players) {
            if (player == null) throw new IllegalArgumentException("A player is NULL");
        }

        this.matchID = matchID;
        this.players = players; // performing no check
        this.numPlayers = players.length;

        // find you and
        for (int i = 0; i < players.length; i++) {
            if (players[i].getUserID() == UserHandler.getUser().getUserId()) {
                thisPlayer = players[i];
                activeTurn = i;
            }
        }
        if (activeTurn == -1) throw new IllegalArgumentException("No player matching the active player");

        // deal with the map
        this.map = new Map();
        Node[] nodes = map.getNodes();
        if (players.length == 2) { // SPECIAL CASE
            nodes[0].setOwner(players[0]);
            nodes[2].setOwner(players[1]);
        } else {
            for (int i = 0; i < players.length; i++) {
                nodes[i].setOwner(players[i]); // assign according to position
                System.out.println(nodes[i].getPosX() + " " + players[i].getNickname());
            }
        }

    }

    public int getMatchID() {
        return matchID;
    }

    public SimpleIntegerProperty roundCountProperty() {
        return roundCount;
    }

    public int getRoundCount() {
        return roundCount.get();
    }

    void setRoundCount(int roundCount) {
        this.roundCount.set(roundCount);
    }

    public SimpleIntegerProperty turnCountProperty() {
        return turnCount;
    }

    public int getTurnCount() {
        return turnCount.get();
    }

    void setTurnCount(int turnCount) {
        this.turnCount.set(turnCount);
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player[] getRankedPlayers() {
        Player[] copy = Arrays.copyOf(players, players.length);
        Arrays.sort(copy);
        return copy;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public Player getActivePlayer() {
        return players[getTurnCount()]; // no null pointer, I think
    }

    // VERY GOOFY SECTION

    /**
     * Only used in the offline map test thingy for seeing how the game behaves at a different position in the queue
     */
    @Deprecated
    void setThisPlayer(Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    @Deprecated
    void setActiveTurn(int activeTurn) {
        this.activeTurn = activeTurn;
    }


    // END OF THAT


    Map getMap() {
        return map;
    }

    /**
     * Sets turn count - resets turn and invrements round if we're at the end of a turn.<br/>
     * Called when a player ends their turn.
     */
    void incrementTurnCount() {
        if (turnCount.get() == numPlayers - 1) {
            turnCount.set(0);
            roundCount.set(getRoundCount() + 1); // we don't check the round count here
        } else {
            //System.out.println("incrementing");
            turnCount.set(getTurnCount() + 1); // fixing incrementing djeez
        }
    }

    public boolean isAtYourTurn() {
        return getTurnCount() == activeTurn;
    }

    // used in MatchThread only, for a specific purpose
    boolean wasAtYourTurn() {
        // if you're last in order, check if turn count is 0, else check if turn count - 1 is your active turn
        return (activeTurn == getPlayers().length - 1? getTurnCount() == 0 : getTurnCount() - 1 == activeTurn);
    }

    public boolean isAtEnd() {
        return getRoundCount() >= ROUND_LIMIT;
    }

    public boolean isCloseToLastRound() {
        return getRoundCount() == ROUND_LIMIT - 7;
    }

    public boolean isInFirstPlace() {
        return getRankedPlayers()[0].equals(getThisPlayer());
    }
    
    public boolean isSabotageAllowed() {
        //System.out.println(getRoundCount() >= SABOTAGE_START?  "correct round" : "not on right round");
        //System.out.println(!sabotaged? "u can sabot" : "not sabot");
        return getRoundCount() >= SABOTAGE_START && !sabotaged;
    }

    void setSabotaged(boolean sabotaged) {
        this.sabotaged = sabotaged;
    }

    /**
     * Sets real owner reference in dummy object, necessary with the way we update things.
     *
     * @param updatedMapComponent   dummy object with updated component info, fetched from DB
     */
    void assignOwner(MapComponent updatedMapComponent) {

        for (Player player : players) {

            if (player.equals(updatedMapComponent.getOwner())) { // if the players match

                updatedMapComponent.setOwner(player); // assign the proper owner
                break; // from inner loop
            }
        }
    }

    /**
     * Updates a player in our list with data from the given dummy player
     *
     * @param updatedPlayer dummy object with updated info, from DB
     */
    void update(Player updatedPlayer) {

        for (Player player : players) {

            if (player.equals(updatedPlayer)) { // if it's this player

                player.setScore(updatedPlayer.getScore()); // update score since that's the only thing
                player.setPresent(updatedPlayer.isPresent()); // update presence status too, now
                break; // gtfo
            }

        }

    }

}
