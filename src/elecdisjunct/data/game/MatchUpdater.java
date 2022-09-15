package elecdisjunct.data.game;

import elecdisjunct.repo.LineDAO;
import elecdisjunct.repo.MatchDAO;
import elecdisjunct.repo.NodeDAO;
import elecdisjunct.repo.PlayerDAO;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class that actually connects the local game state to the database.
 * Overrides several methods in MatchAbstraction to add DAO calls that update the database's
 * info about the game state and fetch updates from it when other players have made their moves.
 *
 * Starting a match:
 * Use constructor 2 for normal players, 3 for the host.
 *
 * Specifically: {@code GameController.setMatch(matchID, players);}
 *
 * @author Tore Bergebakken
 */
public class MatchUpdater extends MatchAbstraction {

    private Match match;

    public MatchUpdater(int matchID, Player[] players) {
        super(matchID, players);
        match = super.getMatch();
        new MatchThread(match, this).start(); // start the damn thread...
    }

    public MatchUpdater(int matchID, Player[] players, boolean host) {
        super(matchID, players);
        match = super.getMatch();
        if (host) {
            initializeMap(); // just calling this to insert the map components
        } else {
            new MatchThread(match, this).start(); // if not host start thread
        }
    }

    @Override
    public boolean isSimulated() {
        return false;
    }

    @Override
    public boolean isTutorial() {
        return false;
    }

    // DB-UPDATING SECTION (allowing updates to happen properly, perhaps)

    @Override
    public boolean claim(MapComponent component) {
        Player previousOwner = component.getOwner();
        if (super.claim(component)) { // if we claimed
            if (!updateComponent(component)) { // ONLY IF IT FAILED
                component.setOwner(previousOwner); // RESET OWNER
                return false;
            } else {
                endTurn();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean upgrade(MapComponent component) {
        int previousLevel = component.getLevel();
        if (super.upgrade(component)) { // if we upgraded
            if (!updateComponent(component)) { // ONLY IF IT FAILED
                component.setLevel(previousLevel); // RESET LEVEL
                return false;
            } else {
                endTurn();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sabotage(MapComponent component){
        if(super.sabotage(component)){
            if(!updateComponent(component)){
                component.setBroken(false);
            }else {
                endTurn();
                return true;
            }
        }
        return false;
    }

    /**
     * Private method that updates either a line or a node's entry in their corresponding DB table.
     *
     * @param component     line/node to update
     * @return              success
     */
    private boolean updateComponent(MapComponent component) {
        if (component instanceof Line) {
            try (LineDAO dao = new LineDAO()) {
                return dao.performUpdate(match.getMatchID(), (Line) component, match.getRoundCount(), match.getTurnCount());
            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "DB update failed (Line)", e);
            }
        } else {
            try (NodeDAO dao = new NodeDAO()) {
                return dao.performUpdate(match.getMatchID(), (Node) component, match.getRoundCount(), match.getTurnCount());
            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "DB update failed (Line)", e);
            }
        }
        return false;
    }

    // ABSTRACTION SECTION - MATCH

    /**
     * Only called by the host. Inserts map components and sets ongoing to true.
     * @return  success
     */
    boolean initializeMap() {

        boolean success = false;
        try (NodeDAO nodeDAO = new NodeDAO()) {
            Node[] nodes = getNodes();
            success = nodeDAO.insertMultiple(match.getMatchID(), nodes);
            if (success) { // TODO remove when/if owners are assigned in 1st DAO method (you don't need this thing for lines tho)
                if (match.getPlayers().length == 2) {
                    nodeDAO.performUpdate(match.getMatchID(), nodes[0], -1, 0);
                    nodeDAO.performUpdate(match.getMatchID(), nodes[2], -1, 0);
                } else {
                    for (int i = 0; i < match.getPlayers().length; i++) {
                        nodeDAO.performUpdate(match.getMatchID(), nodes[i], -1, 0);
                    }
                }
            }
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Inserted nodes");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (success) {
            try (LineDAO lineDAO = new LineDAO()) {
                success = lineDAO.insertMultiple(match.getMatchID(), getLines());
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Inserted lines");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return success;
    }

    /**
     * Called when the client's player leaves the game.<br/>
     * First calls the superclass' method,<br/>
     * then tries to update the DB's entry properly (depending on whose turn it was)<br/>
     * and enters the routine for when the next player also has left if that is the case.<br/>
     *
     * @return  success
     */
    @Override
    public boolean leave() { // TODO delete dat match if all players gone...
        boolean success = false;
        boolean wasAtYourTurn = match.isAtYourTurn();
        if (super.leave()) {
            try (PlayerDAO dao = new PlayerDAO()) {
                if (wasAtYourTurn) {
                    success = dao.leave(match.getMatchID(), match.getThisPlayer(), match.getRoundCount(), match.getTurnCount());
                } else {
                    success = dao.leave(match.getMatchID(), match.getThisPlayer(), match.getRoundCount() + 2, match.getTurnCount()); // adding 2 to roundcount just to be safe
                }
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Left the match");

            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "Could not leave the match", e);
            }
            if (success && wasAtYourTurn) { // ONLY IF IT WAS YOUR TURN...
                try (MatchDAO dao = new MatchDAO()) {
                    success = dao.updateMatch(match); // signalling to other players that this one's turn has passed.
                } catch (SQLException e) {
                    Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "Could not update counts while leaving", e);
                }

                // hacky implementation right now
                if (!match.getActivePlayer().isPresent()) {
                    nextPlayerNotPresentRoutine(); // I *think* this should work
                    if (match.isAtYourTurn()) {
                        System.out.println("DELETE DAT MATCH PLZ - you the last one left, and you leave...");
                    }
                }
            }
        }
        return success;
    }

    /**
     * Calls super's method to update scores, then updates the DB's score data (restoring this client's version if it fails)
     * and performs a special procedure if the match has ended.
     */
    @Override
    void endOfRoundRoutine() {
        Player[] oldPlayers = Arrays.copyOf(match.getPlayers(), match.getPlayers().length);
        super.endOfRoundRoutine();
        // update scores in DB for all players, revert if necessary
        boolean success = false;
        try (PlayerDAO dao = new PlayerDAO()) {
            success = dao.updateScores(match.getMatchID(), match.getPlayers(), match.getRoundCount(), match.getTurnCount());
        } catch (SQLException e) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "DB update failed (Player)", e);
        }
        if (!success) { // if failed
            Player[] failedPlayers = match.getPlayers();
            for (int i = 0; i < failedPlayers.length; i++) {
                failedPlayers[i].setScore(oldPlayers[i].getScore()); // revert to old score for all players
            }
        }

        // AND THEN
        if (match.isAtEnd()) {
            try (MatchDAO dao = new MatchDAO()) {
                success = dao.updateMatch(match); // final update
                success = dao.endMatch(match); // what also needs to be done
            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "Could not update counts while ending match", e);
            }

            if (success) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "MATCH ENDED PROPERLY");
                for (Player player : match.getRankedPlayers()) {
                    System.out.println(player);
                }
            } else {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "MATCH END FAILED WTF");
            }
        }
    }

    /**
     * After your turn, updates DB's round&turn count and starts the update thread
     */
    @Override
    void afterYourTurnRoutine() {
        if (!match.isAtEnd()) {

            try (MatchDAO dao = new MatchDAO()) {
                dao.updateMatch(match);
            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "Could not update counts after this player's turn", e);
            }

            new MatchThread(match, this).start(); // start polling, updating when necessary, exiting run() when it's reached your turn.
        } else {
            System.out.println("GAME HAS GODDAMN ENDED");
        }
    }

    /**
     * do some action in place of the next player (going all 'round the clock if all others have left, theoretically)
     */
    @Override
    void nextPlayerNotPresentRoutine() {
        while (!match.getActivePlayer().isPresent() && !match.isAtYourTurn()) { // you yourself must be present (removing the risk of infinite loop if something is very wrongk
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Skipping " + match.getActivePlayer().getNickname() + "'s turn");
            match.incrementTurnCount(); // just skipping that person's turn

            try (MatchDAO dao = new MatchDAO()) {
                dao.updateMatch(match); // and updating DB counts
            } catch (SQLException e) {
                Logger.getLogger(MatchUpdater.class.getName()).log(Level.SEVERE, "Failed at updating counts in place of another player", e);
            }
        }
    }

    // UPDATE-FETCHING SECTION - THE REAL PARTICULAR MEAT

    /**
     * This method, when called by the MatchThread, updates the client's stored map and player objects.
     * The process is split into four private methods, two of which could have been handled in a much more elegant way.
     * Alas, I may not have time to rectify this.
     */
    void performUpdate() {

        Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Starting update procedure");

        Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Before update: Round " + (match.getRoundCount()+1) + ", turn " + (match.getTurnCount()+1) + " in match with ID " + match.getMatchID());

        updatePlayers();
        updateNodes();
        updateLines();

        updateMatch(); // this is necessary, mind you
        Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "After match update: Round " + (match.getRoundCount()+1) + ", turn " + (match.getTurnCount()+1));


        if (!match.getActivePlayer().isPresent() && match.wasAtYourTurn()) { // if something happened wrongly
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "The player after us doesn't seem to be present");
            nextPlayerNotPresentRoutine(); // yeeeeah
        }


        // make sure to update the round and turn counts only AFTER the update procedure has been performed.
        // then check if it's your turn
        // if so, stop the thread (GUI will know, is activated)

        // perhaps push this out into its own class?
        // that one (or another one) would then be a thread that updates this stuff
        // hm now, wot about synchronization? it shouldn't be an issue as long as we firmly STOP the thread

        Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Finished update procedure");


    }

    private boolean updatePlayers() {
        // fetch list of updated players through DAO
        ArrayList<Player> updatedPlayers = null;
        try (PlayerDAO playerDAO = new PlayerDAO()) {

            updatedPlayers = playerDAO.fetchUpdates(match.getMatchID(), match.getRoundCount(), match.getTurnCount());

        } catch (SQLException sqle) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "Updating players failed", sqle);
        }

        // update our glorious players if needed
        if (updatedPlayers != null) {
            for (Player player : updatedPlayers) {
                match.update(player);
            }
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated " + updatedPlayers.size() + " players");
            return true;
        } else {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated *no* players");
            return false;
        }
    }

    // TODO these two should be one method, at least
    private boolean updateNodes() {
        // fetch list of updated nodes
        ArrayList<Node> updatedNodes = null;
        try (NodeDAO nodeDAO = new NodeDAO()) {

            updatedNodes = nodeDAO.fetchUpdates(match.getMatchID(), match.getRoundCount(), match.getTurnCount());

        } catch (SQLException sqle) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "Updating nodes failed", sqle);
        }
        // update them
        if (updatedNodes != null) {
            for (Node node : updatedNodes) {
                match.assignOwner(node);
                match.getMap().update(node);
            }
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated " + updatedNodes.size() + " nodes");
            return true;
        } else {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated *no* nodes");
            return false;
        }

    }

    private boolean updateLines() {

        // fetch list of updated lines
        ArrayList<Line> updatedLines = null;
        try (LineDAO lineDAO = new LineDAO()) {

            updatedLines = lineDAO.fetchUpdates(match.getMatchID(), match.getRoundCount(), match.getTurnCount());

        } catch (SQLException sqle) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "Updating lines failed", sqle);
        }
        // update them
        if (updatedLines != null) {
            for (Line line : updatedLines) {
                match.assignOwner(line);
                match.getMap().update(line);
            }
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated " + updatedLines.size() + " lines");
            return true;
        } else {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Updated *no* lines");
            return false;
        }

    }

    private void updateMatch() {

        int[] counts = null;

        try (MatchDAO dao = new MatchDAO()) {
            counts = dao.fetchUpdatedCounts(match);
        } catch (SQLException e) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.WARNING, "Failed at fetching updated counts");
        }

        if (counts != null) {
            Logger.getLogger(MatchUpdater.class.getName()).log(Level.INFO, "Actually found updated counts");
            match.setRoundCount(counts[0]);
            match.setTurnCount(counts[1]);
        }
    }

}