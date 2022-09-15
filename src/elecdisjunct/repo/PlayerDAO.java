package elecdisjunct.repo;


import com.sun.istack.internal.NotNull;
import elecdisjunct.data.game.DummyFactory;
import elecdisjunct.data.game.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mia Fornes
 * @author Tore Bergebakken
 */
public class PlayerDAO extends TemplateDAO {

    public PlayerDAO() throws SQLException {}

    /**
     * This method sets a player's presence status to false in the database
     *
     * @param matchID   ID of the ongoing match
     * @param player    the leaving player
     * @param round     what round the player leaves in
     * @param turn      which turn in that round (needed in order to take care of it properly)
     * @return          success/failure
     */
    public boolean leave(int matchID, Player player, int round, int turn) throws SQLException {
        String sqlUpdate = "UPDATE player SET present = 0, updated_round = ?, updated_turn = ? " +
                           "WHERE match_id = ? AND user_id = ?;";
        int res = 0;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlUpdate)) {

            preparedStatement.setInt(1, round);
            preparedStatement.setInt(2, turn);
            preparedStatement.setInt(3, matchID);
            preparedStatement.setInt(4, player.getUserID());
            res = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, "Leaving match failed", e);
        }

        return res == 1;
    }

    /**
     * Method to update the score of all players
     * This is called when a round has ended, by the only client to have responsibility for handling it.
     * Because it already is decided when it has to happen, and because the score will <i>always</i> increase,
     * there is no need to set/check the updated_round and _turn columns in the table - and no need for them to be there at all.
     * ---> nah, listen: we might want to let the players leave the match. seriously. and thus we'll actually need those counts.
     *
     * @param matchID ID of the ongoing match
     * @param players array with players whose score shall get updated
     * @return True if update is successful
     */
    public boolean updateScores(int matchID, @NotNull Player[] players, int round, int turn) throws SQLException {
        String sqlUpdate = "UPDATE player SET score = ?, updated_round = ?, updated_turn = ? " +
                           "WHERE match_id = ? AND user_id = ?;";
        int res = 0;

        //Cleanup.disableAutoCommit(getConnection()); no, we gotta FAIL immediately if this happens.
        getConnection().setAutoCommit(false);

        Savepoint savepoint = getConnection().setSavepoint("Before player update");

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlUpdate)) {

            // update all those players
            for (Player player : players) {

                preparedStatement.setInt(1, player.getScore());
                preparedStatement.setInt(2, round);
                preparedStatement.setInt(3, turn);
                preparedStatement.setInt(4, matchID);
                preparedStatement.setInt(5, player.getUserID());
                res = preparedStatement.executeUpdate();

                if (res != 1) break; // break out of loop, keeping res value
            }

            if (res == 1) {
                getConnection().commit();
            } else {
                getConnection().rollback(savepoint);
            }

        } catch (SQLException e) {
            getConnection().rollback(savepoint);
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, "Updating player score failed", e);
        } finally {
            Cleanup.enableAutoCommit(getConnection());
        }

        return res == 1;
    }


    /**
     * Fetching all players associated with a particular match
     * checking <i>when</i> they were updated cuz players may <i>leave</i>
     *
     * @param matchID    ID of the ongoing match
     * @param lastRound  last round with known state
     * @param lastTurn   last turn with known state
     * @return           an ArrayList with the updated players
     */
    public ArrayList<Player> fetchUpdates(int matchID, int lastRound, int lastTurn) {

        String sqlFetch = "SELECT user_id, score, present FROM player " +
                          "WHERE match_id = ? AND (updated_round > ? OR (updated_round = ? AND updated_turn > ?));";

        ArrayList<Player> updatedPlayers = new ArrayList<>();

        try (PreparedStatement fetchStatement = getConnection().prepareStatement(sqlFetch)) {

            fetchStatement.setInt(1, matchID);
            fetchStatement.setInt(2, lastRound);
            fetchStatement.setInt(3, lastRound);
            fetchStatement.setInt(4, lastTurn);

            try (ResultSet res = fetchStatement.executeQuery()) {

                while (res.next()) {

                    // setting up a dummy object
                    updatedPlayers.add(DummyFactory.createDummyPlayer(res.getInt("user_id"), res.getInt("score"), res.getBoolean("present")));
                }

            } catch (SQLException sqle) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.WARNING, "Parsing ResultSet failed while fetching Player updates", sqle);
            }

        } catch (SQLException sqle) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.WARNING, "Could not fetch Player updates", sqle);
        }

        return updatedPlayers;
    }

    /**
     * THIS IS YOUR METHOD, Patrick
     * @param matchID    of connected match (it IS the MATCH's ID, NOT the game code!)
     * @return          sorted list of players WITHOUT ANY MORE FUSS!
     */
    public ArrayList<Player> fetchPlayersInLobby(int matchID) {

        String sqlFetch = "SELECT p.user_id, nickname, color from player p, users u " +
                          "WHERE match_id = ? AND p.user_id = u.user_id ORDER BY active_in_turn;";

        ArrayList<Player> playersInLobby = new ArrayList<>();

        try (PreparedStatement fetchStatement = getConnection().prepareStatement(sqlFetch)) {

            fetchStatement.setInt(1, matchID);

            try (ResultSet res = fetchStatement.executeQuery()) {

                while (res.next()) {

                    // setting up a proper Player
                    playersInLobby.add(DummyFactory.createLobbyPlayer(res.getInt("user_id"), res.getString("nickname"), res.getString("color")));
                }

            } catch (SQLException sqle) {
                Logger.getLogger(PlayerDAO.class.getName()).log(Level.WARNING, "Parsing ResultSet failed while fetching players in lobby", sqle);
                return null;
            }

        } catch (SQLException sqle) {
            Logger.getLogger(PlayerDAO.class.getName()).log(Level.WARNING, "Could not fetch players in lobby", sqle);
            return null;
        }


        return playersInLobby.size() > 0? playersInLobby : null;
    }
}

