package elecdisjunct.data.game;

import elecdisjunct.repo.MatchDAO;
import elecdisjunct.view.controller.GameController;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread that periodically does a swift DB query to see if state has been updated,
 * calling the update method in MatchUpdater if there is need to do so.
 * Started when the player has made their move or just joined a game.
 *
 * @author Tore Bergebakken
 */
class MatchThread extends Thread {

    private Match match;
    private MatchUpdater updater;

    MatchThread(Match match, MatchUpdater updater) {

        this.match = match;
        this.updater = updater;

    }

    @Override
    public void run() {

        Logger.getLogger(MatchThread.class.getName()).log(Level.INFO, "Update thread has started running");

        int waitCounter = 0;

        // leaves if it's your turn or the game is @ last round
        while (!match.isAtYourTurn() && !match.isAtEnd() && match.getThisPlayer().isPresent()) { // added check if YOU ARE PRESENT so it doesn't continue running

            // goes to sleep
            try {
                sleep(460);
            } catch (InterruptedException e) {
                Logger.getLogger(MatchThread.class.getName()).log(Level.INFO, "Sleep was interrupted", e);
            }

            boolean shouldUpdate = true; // will try updating if query fails
            // actual check
            try (MatchDAO dao = new MatchDAO()) {
                shouldUpdate = dao.checkForUpdate(match.getMatchID(), match.getRoundCount(), match.getTurnCount());
            } catch (SQLException e) {
                Logger.getLogger(MatchThread.class.getName()).log(Level.SEVERE, "Could not check for updates", e);
            }

            if (shouldUpdate) {
                // JavaFX complains (understandably) when other threads than the one(s) it manage(s)
                // modify the state of, for example, a property that has a ChangeListener attached.
                Platform.runLater(() -> updater.performUpdate());
                waitCounter = 0; // reset counter
            } else {
                waitCounter++;
            }

            // logging
            if ((waitCounter+1) % 10 == 0) Logger.getLogger(MatchThread.class.getName()).log(Level.INFO, "Thread has polled DB " + (waitCounter+1) + " times.");

            // patience treshold (does nothing at the moment)
            if (waitCounter % 25 == 0) { // 360 -> three minutes? (adjusted)
                // TODO take action and set the next player to not present (BUT only if this is the client running right before it?)
                Logger.getLogger(MatchThread.class.getName()).log(Level.INFO, "Patience treshold exceeded");
                // there'd then be something here that would (eventually) forward the game (in DB too) and then make this thread jump out

                // instead, lets see...
                Platform.runLater(() -> updater.performUpdate()); // just run the update procedure
                //waitCounter = 0; // reset count --> nah, that's unnecessary
            }

        }

        Logger.getLogger(MatchThread.class.getName()).log(Level.INFO, "Update thread has finished running");


    }

}
