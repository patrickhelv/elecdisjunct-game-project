package elecdisjunct.repo;

import elecdisjunct.data.game.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 *
 * @author Mia Fornes
 * @author Victoria Blichfeldt
 */

public class StatsDAO extends TemplateDAO {

    public StatsDAO() throws  SQLException {

    }

    /**
     * Method that is used to find the number of games played by a specific player
     *
     * @param userID to the specific player
     * @return number of games played by the player
     */

    public String getGamesPlayed(int userID) {

        String gamesPlayed = null;

        String sqlQuery = "SELECT COUNT(match_id) AS games_played FROM player WHERE user_id = ?;";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet res = preparedStatement.executeQuery()) {

                while (res.next()) {

                    gamesPlayed = res.getString("games_played");

                }

            } catch (SQLException e) {
                Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (SQLException e) {
            Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return gamesPlayed;

    }

    /**
     * Method that is used to find the number of games won by a specific player
     *
     * @param userID to the specific player
     * @return number of games won by the player
     */
    public String getGamesWon(int userID) {

        String gamesWon = null;
        
        String sqlQuery = "SELECT COUNT(match_id) AS games_won FROM player WHERE active_in_turn = 1 AND user_id = ?;";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet res = preparedStatement.executeQuery()) {

                while (res.next()) {

                    gamesWon = res.getString("games_won");

                }

            } catch (SQLException e) {
                Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (SQLException e) {
            Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return gamesWon;
    }

    /**
     * Method that is used to find the highest score achieved by a specific player
     *
     * @param userID to the specific player
     * @return the highest score to a player
     */
    public String getHighscore(int userID) {

        String highScore = null;


        String sqlQuery = "SELECT MAX(score) AS highscore FROM player WHERE user_id = ?;";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet res = preparedStatement.executeQuery()) {

                while (res.next()) {

                    highScore = res.getString("highscore");

                }

            } catch (SQLException e) {
                Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (SQLException e) {
            Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
        }


        return highScore;

    }

    /**
     * Method that is used for the global statistics
     * The global statistics contains the nickname, score, and number of nodes and lines to the 20 highest ranked players (by score)
     *
     * @return the list of players
     */
    public ObservableList<Player> getGlobalStats() {

        ObservableList<Player> observableList = FXCollections.observableArrayList();


        String sqlQuery = "SELECT users.nickname, player.score, COUNT(node.match_id) 'totnodes', linelist.totlines FROM player, users, node " +
                "LEFT JOIN (SELECT player.match_id 'matchID', line.owner 'lineowner', COUNT(line.match_id) 'totlines' FROM player, users, line, game " +
                "WHERE player.user_id = users.user_id AND player.match_id = line.match_id AND player.user_id = line.owner AND player.match_id = game.match_id AND game.ongoing = 0 AND game.round > 19 " +
                "GROUP BY lineowner, player.score, player.match_id " +
                "ORDER BY score) AS linelist ON linelist.matchID = node.match_id " +
                "WHERE player.user_id = users.user_id AND player.match_id = node.match_id AND player.user_id = node.owner " +
                "AND player.match_id = linelist.matchID AND player.user_id = linelist.lineowner " +
                "GROUP BY users.nickname, player.score, linelist.totlines " +
                "ORDER BY score DESC LIMIT 20;";

        try (ResultSet res = getConnection().createStatement().executeQuery(sqlQuery)) {

            while (res.next()) {

               observableList.add(new Player(res.getString("nickname"), res.getInt("score"), res.getInt("totnodes"), res.getInt("totlines")));

            }

        } catch (SQLException e) {
            Logger.getLogger(StatsDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return observableList;

    }

 }

