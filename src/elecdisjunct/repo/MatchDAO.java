package elecdisjunct.repo;

import elecdisjunct.data.game.Match;
import elecdisjunct.data.game.Player;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tore Bergebakken
 */
public class MatchDAO extends TemplateDAO {

    public MatchDAO() throws SQLException {
        super();
    }

    /**
     * Pings the database to see if the match's round and/or turn count
     * indicate that changes have been made.
     *
     * @param matchID
     * @param lastRound
     * @param lastTurn
     * @return
     */
    public boolean checkForUpdate(int matchID, int lastRound, int lastTurn) {

        String sqlPing = "SELECT match_id, round, turn FROM game WHERE game.match_id = ? " +
                         "AND (round > ? OR (round = ? AND turn > ?) OR ongoing = 0);"; // ADDED THAT LAST THING RIGHT NOW

        try (PreparedStatement turnStatement = getConnection().prepareStatement(sqlPing)) {

            turnStatement.setInt(1, matchID);
            turnStatement.setInt(2, lastRound);
            turnStatement.setInt(3, lastRound); // whoops
            turnStatement.setInt(4, lastTurn);

            try (ResultSet res = turnStatement.executeQuery()) {
                if (res.next() && res.getInt("match_id") == matchID) {

                    int round = res.getInt("round");
                    int turn = res.getInt("turn");

                    return round > lastRound || (round == lastRound && turn > lastTurn);
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not update match", e);
        }

        return false;
    }

    public int[] fetchUpdatedCounts(Match toUpdate) {

        int[] counts = null;

        String sqlFetch = "SELECT round, turn FROM game WHERE match_id = ?";

        try (PreparedStatement fetchStatement = getConnection().prepareStatement(sqlFetch)) {

            fetchStatement.setInt(1, toUpdate.getMatchID());

            try (ResultSet res = fetchStatement.executeQuery()) {

                if (res.next()) {
                    counts = new int[2];
                    counts[0] = res.getInt("round");
                    counts[1] = res.getInt("turn");
                    System.out.println("Counts found: round " + counts[0] + " turn " + counts[1]);
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not fetch counts", e);
            }

        } catch (SQLException e) {
            Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not create fetch statement", e);
        }

        return counts;

    }

    public boolean startMatch(Match match) {

        int res = 0;

        String sqlTurnIncrement = "UPDATE game SET ongoing = 1 WHERE match_id = ?";

        try (PreparedStatement turnStatement = getConnection().prepareStatement(sqlTurnIncrement)) {

            turnStatement.setInt(1, match.getMatchID());
            res = turnStatement.executeUpdate();

        } catch (SQLException e) {
            Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not start match", e);
        }

        return res == 1;

    }

    public boolean updateMatch(Match match) {

        String sqlTurnIncrement = "UPDATE game SET round = ?, turn = ? WHERE match_id = ?";

        try (PreparedStatement turnStatement = getConnection().prepareStatement(sqlTurnIncrement)) {

            turnStatement.setInt(1, match.getRoundCount());
            turnStatement.setInt(2, match.getTurnCount());
            turnStatement.setInt(3, match.getMatchID());
            turnStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not update match", e);
        }

        return false;
    }

    public boolean endMatch(Match match) {

        String sqlUpdatePlayers = "UPDATE player SET active_in_turn = ? WHERE user_id = ? AND match_id = ?";
        String sqlNoLongerOngoing = "UPDATE game SET ongoing = 0 WHERE match_id = ?";

            try {

                getConnection().setAutoCommit(false);

                Savepoint savepoint = getConnection().setSavepoint("Before match end");

                try (PreparedStatement playerStatement = getConnection().prepareStatement(sqlUpdatePlayers);
                     PreparedStatement ongoingStatement = getConnection().prepareStatement(sqlNoLongerOngoing)) {

                    Player[] rankedPlayers = match.getRankedPlayers();

                    for (int i = 0; i < rankedPlayers.length; i++) {

                        playerStatement.setInt(1, i + 1); // rank from 1 to 2-4
                        playerStatement.setInt(2, rankedPlayers[i].getUserID());
                        playerStatement.setInt(3, match.getMatchID());

                        if (playerStatement.executeUpdate() != 1) {
                            getConnection().rollback(savepoint);
                            return false;
                        }

                    } // done with the players

                    ongoingStatement.setInt(1, match.getMatchID());
                    if (ongoingStatement.executeUpdate() != 1) {
                        getConnection().rollback(savepoint);
                        return false;
                    } else {
                        return true; // uhhh okay
                    }

                } catch (SQLException e) {
                    Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not end the match", e);
                    Cleanup.performRollback(getConnection(), savepoint);
                } finally {
                    Cleanup.enableAutoCommit(getConnection());
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchDAO.class.getName()).log(Level.SEVERE, "Could not set dat autocommit or savepoint", e);

            }

        return false;

    }

}
