package elecdisjunct.data.game;

import elecdisjunct.data.user.User;
import elecdisjunct.data.user.UserHandler;

/**
 * Inherits the MatchAbstraction class, providing (for now) a simple way to test the game's functionality
 * without the database-querying code needing to be written properly first.
 *
 * Might serve as a stepping stone to an interactive tutorial.
 * For now it just prints indications of methods being called when they should be.
 *
 * @author Tore Bergebakken
 */
public class MatchSimulator extends MatchAbstraction {

    private Match match;

    public MatchSimulator() {
        super();
        match = super.getMatch();
    }

    public MatchSimulator(int matchID, Player[] players) {
        super(matchID, players);
        match = super.getMatch();
    }

    @Override
    public boolean isSimulated() {
        return true;
    }

    @Override
    public boolean isTutorial() {
        return false; // TODO will be true when (ahem, *if*) this is a proper tutorial
    }

    @Override
    void endOfRoundRoutine() {
        super.endOfRoundRoutine();
        System.out.println("round end should proceed normally?");
    }

    @Override
    void nextPlayerNotPresentRoutine() {
        System.out.println("damn you daniel, why u go afk");
    }

    @Override
    void afterYourTurnRoutine() {
        System.out.println("It's the end of my turn. Wooo.");
    }

    @Deprecated
    public void changeYourself() {
        User user = UserHandler.getUser();
        int activeTurn = -1;
        Player theNewYou = null;
        for (int i = 0; i < match.getPlayers().length; i++) {
            if (match.getPlayers()[i].getUserID() == user.getUserId()) {
                activeTurn = i;
                theNewYou = match.getPlayers()[i];
            }
        }
        if (theNewYou != null && activeTurn != -1) {
            match.setActiveTurn(activeTurn);
            match.setThisPlayer(theNewYou);
        }
    }

}
