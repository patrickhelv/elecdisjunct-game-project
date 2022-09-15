package elecdisjunct.repo;

import com.mchange.v1.util.CleanupUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class contains method used by Lobby
 * It is mostly used to retrieve information
 * from the database.
 *
 * It is also used to update some information
 * stored in the database.
 *
 * @author patrick Helvik Legendre
 */

public class UpdateLobbyDAO extends TemplateDAO{
    private Connection connection;

    /**
     * creates a constructor that extends templateDAO connection
     * @throws SQLException
     */

    public UpdateLobbyDAO() throws SQLException{
        connection = super.getConnection();
    }

    /**
     * This method is used when a player leaves the lobby and that
     * player is not the host. The method runs as one transaction.
     *
     * It will first update the users table and then delete the
     * player's reference in the player table.
     *
     * @param gamecode the gamecode for the lobby.
     * @param userId the user's userId.
     * @param matchId the matchId for the match created by the host.
     * @return a boolean as a status.
     */
    public boolean userLeft(int gamecode, int userId, int matchId){
        PreparedStatement preparedStatement = null;
        if(gamecode == 0 || gamecode < 0 || userId == 0 || userId < 0 || matchId == 0 || matchId < 0){
            return false;
        }
        try {
            connection.setAutoCommit(false);

            String sqlstatement = "UPDATE users set game_code = NULL where game_code = ? and user_id = ?";
            String sqlstatement2 = "DELETE FROM player where match_id = ? and user_id = ?";
            try {
                preparedStatement = connection.prepareStatement(sqlstatement);
                preparedStatement.setInt(1, gamecode);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(sqlstatement2);
                preparedStatement.setInt(1, matchId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();


            } catch (SQLException sq) {
                sq.printStackTrace();
            } finally {
                Cleanup.closePreparedStatement(preparedStatement);
            }

            connection.commit();
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        Cleanup.enableAutoCommit(connection);
        return true;
    }

    /**
     * This method is used to return an ArrayList of String
     * that represents colors to a method in Lobby.
     *
     * This ArrayList contains each color assigned to a player
     * in that match.
     *
     * @param matchId the matchId for the match created by the host.
     * @return an ArrayList of String filled with colors.
     */

    public ArrayList<String> getColors(int matchId){
        PreparedStatement preparedStatement = null;
        ResultSet res = null;
        ArrayList<String> colors = new ArrayList<>();
        if(matchId == 0 || matchId < 0){
            return null;
        }
        String sqlstatement = "SELECT player.color FROM player where player.match_id = ? ";
        try {
            preparedStatement = connection.prepareStatement(sqlstatement);
            preparedStatement.setInt(1, matchId);
            res = preparedStatement.executeQuery();
            while (res.next()){
                 colors.add(res.getString("color"));
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        if(colors == null){
            return null;
        }
        return colors;
    }

    /**
     * method that retrieves matchId from the database using the gamecode for
     * that specific lobby.
     *
     * @param gamecode the gamecode for the lobby
     * @return match_id so we can update it on the client side
     *
     */
    public int getMatchid(int gamecode){
        String sqlstatement2 = "SELECT match_id from lobby where game_code = ? ";
        ResultSet res = null;
        PreparedStatement preparedStatement = null;
        int result = 0;
        if(gamecode == 0 || gamecode < 0){
            return -1;
        }
        try {
            preparedStatement = connection.prepareStatement(sqlstatement2);
            preparedStatement.setInt(1, gamecode);
            res = preparedStatement.executeQuery();
            while (res.next()){
                result = res.getInt("match_id");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        System.out.println("gamecode " + result);
        if(result == 0 ){
            return -1;
        }
        return result;
    }

    /**
     * Verify game code, takes as an input gamecode and
     * returns true if the gamecode exists or else it return false.
     *
     * This method is used for users that wants to join a lobby.
     *
     * @param gamecode the gamecode for the lobby.
     * @return a boolean that gives us a status
     */

    public boolean verifyGameCode(int gamecode){
        String sqlstatement1 = "SELECT game_code FROM lobby where game_code = ?;";
        ResultSet res = null;
        PreparedStatement preparedStatement = null;
        int result = 0;
        if(gamecode == 0 || gamecode < 0){
            return false;
        }
        try {
            preparedStatement = connection.prepareStatement(sqlstatement1);
            preparedStatement.setInt(1, gamecode);
            res = preparedStatement.executeQuery();
            while(res.next()){
                result = res.getInt("game_code");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return result == gamecode;
    }

    /**
     * Method that returns a String that represents a color for that specific player.
     *
     * This method is used when a player desires to change his
     * color in the lobby for a new color.
     *
     * @param userId the user's userId.
     * @param matchId the matchId for the match created by the host.
     * @return a String for one color for that player.
     */

    public String getPlayerColor(int userId, int matchId){
        String sqlstatement = "SELECT color FROM player where player.user_id = ? and player.match_id = ?";
        PreparedStatement preparedStatement = null;
        String color = null;
        ResultSet res = null;
        if(userId == 0 || userId < 0 || matchId == 0 || matchId < 0){
            return null;
        }
        try {
            preparedStatement = connection.prepareStatement(sqlstatement);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, matchId);
            res = preparedStatement.executeQuery();
            if(res.next()){
                color = res.getString("color");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        System.out.println(color);
        return color;
    }

    /**
     * Method that is checking in the database in the game table with a
     * specific matchId to see if a game has started or not.
     *
     * If the game has started will return true else false.
     * This method is used to know if the player host has clicked on
     * start game in the lobby controller. If he has other players need to
     * get updated and moved to the match.
     *
     * @param matchId the matchId for the match created by the host.
     * @return a boolean that represents a status.
     */

    public boolean checkIfGameStarted(int matchId){
        String sqlstatement = "SELECT game.match_id FROM game WHERE game.match_id = ? and game.ongoing = 1";
        PreparedStatement preparedStatement = null;
        ResultSet res = null;
        boolean ok = false;
        if(matchId == 0 || matchId < 0){
            return false;
        }
        try {
            preparedStatement = connection.prepareStatement(sqlstatement);
            preparedStatement.setInt(1, matchId);
            res = preparedStatement.executeQuery();
            System.out.println(matchId);
            if(res.next()) {
                ok = true;
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return ok;
    }

    /**
     * Method that runs as one transaction. This method is used when
     * a player has chosen a new color in Lobby. This is the method to update
     * the database with this new color.
     *
     * First it will check with the database if the color has already been
     * chosen by another player in the meantime. If the response is true nothing will happen
     * if it is false it will change the color of this player to the color desired by
     * updating the database with the new color.
     *
     * @param matchId the matchId for the match created by the host.
     * @param userId the user's userId.
     * @param color a string that represents a color.
     * @return a boolean as a status.
     */

    public boolean changeColor(int matchId, int userId, String color) {
        String sqlstatement = "UPDATE player set player.color = ? where player.match_id = ? and player.user_id = ?;";
        ArrayList<String> colors;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = null;
            if (matchId == 0 || matchId < 0 || userId == 0 || userId < 0 || color == null) {
                Cleanup.performRollback(connection);
                Cleanup.enableAutoCommit(connection);
                return false;
            }
            colors = getColors(matchId);
            for (int i = 0; i < colors.size(); i++) {
                System.out.println("colors size " + colors.size());
                System.out.println("color choosen " + color);
                if (colors.contains(color)) {
                    Cleanup.performRollback(connection);
                    Cleanup.enableAutoCommit(connection);
                    return false;
                }
            }
            try {
                preparedStatement = connection.prepareStatement(sqlstatement);
                preparedStatement.setString(1, color);
                preparedStatement.setInt(2, matchId);
                preparedStatement.setInt(3, userId);
                preparedStatement.executeUpdate();
            } catch (SQLException sq) {
                sq.printStackTrace();
            } finally {
                Cleanup.closePreparedStatement(preparedStatement);
            }
            connection.commit();
        } catch (SQLException sq) {
            sq.printStackTrace();
        } finally {
            Cleanup.enableAutoCommit(connection);
        }
        return true;
    }
}

