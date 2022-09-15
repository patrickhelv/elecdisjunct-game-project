package elecdisjunct.repo;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * This class is used to update the database in one transaction.
 * Most of the methods are used to update the lobby directly.
 *
 * @author patrick Helvik Legendre
 */

public class LobbyDAO extends TemplateDAO {

    private Connection connection;
    private int matchId;
    private int amountOfPlayers = 0;

    /**
     * creates a constructor that extends templateDAO connection
     * @throws SQLException
     */

    public LobbyDAO() throws SQLException{
        this.connection = super.getConnection();
    }

    /**
     * createLobby is a method that calls 2 private methods
     * where they update the lobby table by adding a new reference
     * in the database when a user creates a lobby.
     *
     * This method is updating the database in
     * one transaction. If something fails, it will call
     * rollback.
     *
     * @param userId userId the user's userId.
     * @param gamecode is the gamecode for the lobby.
     * @return a boolean that gives us the status.
     *
     */

    public boolean createLobby(int userId, int gamecode){
        boolean ok = false;
        if(userId == 0 || userId < 0 || gamecode == 0 || gamecode < 0){
            return false;
        }
        try {
            connection.setAutoCommit(false);
            ok = createMatchId();
            if (ok == false) {
                transactionFailed();
                return false;
            }
            ok = insertLobby(userId, gamecode);
            if (ok == false) {
                transactionFailed();
                return false;
            }
            connection.commit();
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.enableAutoCommit(connection);
        }
        System.out.println("created lobby successfully");
        return true;
    }


    /**
     *
     * This private method is called by createLobby to create a new game and retrieve
     * the matchId from the database.
     *
     * @return a boolean as a status
     */
    private boolean createMatchId(){
        String sqlstatement1 = "INSERT INTO game (ongoing) values (0)";
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(sqlstatement1);
        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }
        ResultSet res = null;
        int result =  0;
        Statement statement = null;
        String sqlstatement2 = "SELECT game.match_id FROM game where game.ongoing = '0'";
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sqlstatement2);
            while (res.next()) {
                result = res.getInt("match_id");
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
            return false;
        } finally {
            Cleanup.closeResultSet(res);
            Cleanup.closeStatement(statement);
        }
        if(result== 0){
            return false;
        }
        matchId = result;
        return true;
    }

    /**
     * Private method called by createLobby.
     *
     * This method is used to insert in the lobby table
     * the gamecode, the userId and the matchId.
     *
     * It will also insert into player table
     * matchId and the userId.
     *
     * At last it will update the users table by adding the gamecode.
     *
     * @param userId userId the user's userId.
     * @param gamecode is the gamecode for the lobby.
     * @return a boolean as a status
     */
    private boolean insertLobby(int userId, int gamecode){
        String sqlstatemtent3 = "INSERT into lobby (game_code, host, match_id) VALUES (?, ?, ?);";
        String sqlstatement4 = "insert into player (match_id, user_id, score, color, active_in_turn) VALUES (?, ?, 0, 'red', 1);";
        String sqlstatement5 = "UPDATE users set game_code = ? where user_id = ?;";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlstatemtent3);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.setInt(2,userId);
            preparedStatement.setInt(3, matchId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement4);
            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement5);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.setInt(2,userId);
            preparedStatement.executeUpdate();

        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }finally {
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return true;
    }

    /**
     * method that calls 1 private method that updates the database when users
     * are starting a new game.
     *
     * The database is updated in one transaction if something fails
     * it will rollback.
     *
     * @param matchId used for the private method.
     * @param gamecode used for the private method.
     * @return a boolean as a status value.
     *
     */


    public boolean startGame(int matchId, int gamecode){
        boolean ok = false;
        if(matchId == 0 || matchId < 0 || gamecode == 0 || gamecode < 0){
            return false;
        }
        try {
            connection.setAutoCommit(false);
            ok = UpdateMatch(matchId, gamecode);
            if (ok == false) {
                transactionFailed();
                return false;
            }
            connection.commit();
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.enableAutoCommit(connection);
        }
        System.out.println("starting game successfully");
        return true;
    }

    /**
     * private method that updates match table in the database and
     * updates the user with a gamecode. This method is called by
     * is called by startGame.
     * In the database all users with that gameccode will
     * set their "game_code" value in the database to null
     *
     * update lobby runs a statement to the database which deletes the
     * lobby with that gamecode.
     *
     * This method is used when the game is starting,
     * every reference to the lobby will be deleted and
     * update the necessary for switching from lobby to the match.
     *
     * @param matchId used to update game.
     * @param gamecode used to update users and lobby.
     * @return a boolean value as a status.
     */

    private boolean UpdateMatch(int matchId, int gamecode){
        PreparedStatement preparedStatement = null;
        String sqlstatement1 = "UPDATE game set ongoing = '1', turn = '0', round = '0' where match_id = ?;";
        String sqlstatement2 = "UPDATE users set game_code = NULL where game_code = ?;";
        String sqlstatement3 = "DELETE FROM lobby where game_code = ?;";

        try {
            preparedStatement = connection.prepareStatement(sqlstatement1);
            preparedStatement.setInt(1, matchId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement2);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement3);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.executeUpdate();

        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return true;
    }

    /**
     * Delete lobby is a method that calls 3 private methods which
     * executes sql queries to the database to keep it updated.
     *
     * It will execute this method in one transaction, if something
     * fails, the method will rollback.
     *
     * The method is build around deleting the necessary
     * references used when the lobby was created.
     *
     * @param gamecode is the gamecode from that lobby.
     * @param userId userId the user's userId.
     * @param matchId the matchId for the match created by the host.
     * @return a boolean which represents a status value.
     *
     */


    public boolean deleteLobby(int gamecode, int userId, int matchId) {
        if (gamecode < 0 || gamecode == 0 || userId < 0 || userId == 0 || matchId < 0 || matchId == 0) {
            return false;
        }
        try {
            connection.setAutoCommit(false);
            boolean ok = false;
            ok = delete(gamecode, matchId);
            if (ok == false) {
                transactionFailed();
                return false;
            }
            connection.commit();
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.enableAutoCommit(connection);
        }
        System.out.println("lobby has been successfully deleted");
        return true;
    }

    /**
     *
     * This private method will first update all the users
     * that had a reference to that gamecode.
     * This method is called by deleteLobby.
     *
     * If the host leaves every player present in that lobby will return to main menu
     * and will have all of their references to that lobby deleted.
     *
     * It will then delete in the table lobby and player and match.
     * It will delete based on the reference from the lobby.
     *
     * @param gamecode is the gamecode from that lobby.
     * @param matchId the matchId for the match created by the host.
     * @return a boolean as a status.
     */

    private boolean delete(int gamecode, int matchId){
        String sqlstatement1 = "UPDATE users set game_code = NULL where game_code = ?;";
        String sqlstatement2 = "DELETE FROM lobby where game_code = ?";
        String sqlstatement3 = "DELETE FROM player where match_id = ? ";
        String sqlstatement4 = "DELETE FROM game where match_id = ?;";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlstatement1);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement2);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement3);
            preparedStatement.setInt(1, matchId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(sqlstatement4);
            preparedStatement.setInt(1, matchId);
            preparedStatement.executeUpdate();

        }catch (SQLException sq) {
            sq.printStackTrace();
        }finally {
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return true;
    }

    /**
     * This method is used when a player joins a lobby with his gamecode
     * The first private method will update the user table and the
     * second one will update the player table.
     *
     * The method is in one transaction if something fails it will
     * rollback.
     *
     * @param gamecode is the gamecode from that lobby.
     * @param userId userId the user's userId.
     * @return a boolean that gives us a status
     *
     */

    public boolean joinLobby(int gamecode, int userId, int matchId){
        boolean ok = false;
        if(gamecode == 0 || gamecode < 0 || userId == 0 || userId < 0 || matchId == 0 || matchId < 0){
            return false;
        }
        try {
            connection.setAutoCommit(false);
            ok = updateUser(userId, gamecode);
            if(ok == false){
                transactionFailed();
                return false;
            }
            ok = updatePlayer(userId, matchId, gamecode);
            if (ok == false) {
                transactionFailed();
                return false;
            }
            connection.commit();
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.enableAutoCommit(connection);
        }
        System.out.println("Joined lobby successfully");
        return true;
    }

    /**
     * Method that counts every player in one lobby
     * using the gamecode. This method is called by
     * updatePlayer.
     *
     * @param gamecode is the gamecode for the lobby.
     * @return an int for all the players in that lobby
     */

    private int getAmountOfPlayers(int gamecode){
        String sqlstatement2 = "select COUNT(user_id) as number_of_users from users where game_code = ?;";
        ResultSet res = null;
        PreparedStatement preparedStatement = null;
        int amountOfPlayers = 0;
        if(gamecode == 0 || gamecode < 0){
            return -1;
        }
        try {
            preparedStatement = connection.prepareStatement(sqlstatement2);
            preparedStatement.setInt(1, gamecode);
            res = preparedStatement.executeQuery();
            while (res.next()){
                amountOfPlayers = res.getInt("number_of_users");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }finally {
            Cleanup.closeResultSet(res);
            Cleanup.closePreparedStatement(preparedStatement);
        }
        if(amountOfPlayers == -1 || amountOfPlayers == 0 || amountOfPlayers > 4){
            return -1;
        }
        return amountOfPlayers;
    }

    /**
     * This private method will update one user that has
     * entered the gamecode correctly for that specific
     * lobby.
     * updateUser is called by joinLobby.
     *
     * @param userId userId the user's userId.
     * @param gamecode is the gamecode from that lobby.
     * @return a boolean as a status.
     */

    private boolean updateUser(int userId, int gamecode){
        String sqlstatement3 = "UPDATE users set game_code = ? where user_id = ?;";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlstatement3);
            preparedStatement.setInt(1, gamecode);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }finally {
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return true;
    }

    /**
     *
     * This is a private method that will update the player table.
     * It will first check if the player amount is less than 4, then
     * it will retrieve the colors from all the players present in the lobby.
     *
     * Then it will call a private method and transfer the ArrayList of colors to
     * that method. The assignColor method will assign a color that has not been
     * claimed yet.
     *
     * This method is called by joinLobby.
     *
     * If nothing has returned false. The method will
     * update the database to indicate that a player has joined.
     *
     * @param userId userId the user's userId.
     * @param matchId the matchId for the match created by the host.
     * @param gamecode is the gamecode from that lobby.
     * @return a boolean as a status.
     */

    private boolean updatePlayer(int userId, int matchId, int gamecode){
        ArrayList<String> colors;
        String color = "";
        amountOfPlayers = getAmountOfPlayers(gamecode);
        if(amountOfPlayers == -1){
            return false;
        }
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){
            colors = updateLobbyDAO.getColors(matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }
        if(colors == null){
            System.out.println("error while checking color");
            return false;
        }
        color = assignColor(colors);
        if(color == null){
            System.out.println("all colors claimed, color null");
            return false;
        }
        String sqlstatement4 = "insert into player (match_id, user_id, score, color, active_in_turn) VALUES (?, ?, 0, ?, ?);";
        PreparedStatement preparedStatement = null;
        try{
            amountOfPlayers++;
            preparedStatement = connection.prepareStatement(sqlstatement4);
            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, color);
            preparedStatement.setInt(4, amountOfPlayers);
            preparedStatement.executeUpdate();
        }catch (SQLException sq){
            sq.printStackTrace();
            return false; // RETURN FALSE IF SOMETHING WENT WRONG OMG
        }finally {
            Cleanup.closePreparedStatement(preparedStatement);
        }
        return true;

    }

    /**
     *
     * Private method called by updatePlayer.
     * This method is used to know what colors remains after players claimed
     * their colors.
     *
     * After that it the player will be given a color from what is left. But the
     * player can still change his color. The color already claimed will be free
     * so that another player can pick it as their color.
     *
     * @param colors is an ArrayList of colors claimed by the player in the database.
     * @return a single String that represents the color that has not been claimed.
     */

    private String assignColor(ArrayList<String> colors){
        String color = "";
        int claimed = 0;
        String[] colorpreset = new String[]{"RED", "GREEN", "BLUE", "YELLOW", "PURPLE", "ORANGE" };
        for(int i = 0; i < colors.size(); i++){ //goes through table and checks if a color has been claimed if it has been claimed it will set the array location to null
            if(colors.get(i).equals("RED")){
                colorpreset[0] = null;
            }else if(colors.get(i).equals("GREEN")){
                colorpreset[1] = null;
            }else if(colors.get(i).equals("BLUE")){
                colorpreset[2] = null;
            }else if(colors.get(i).equals("YELLOW")){
                colorpreset[3] = null;
            }else if(colors.get(i).equals("PURPLE")){
                colorpreset[4] = null;
            }else if(colors.get(i).equals("ORANGE")) {
                colorpreset[5] = null;
            }
        }
        for(int i = 0; i < colorpreset.length; i++){
            if(colorpreset[i] != null){ //goes though and checks if a color is not null that means has not been claimed yet.
                color = colorpreset[i]; //takes the first one that is not claimed.
                return color;
            }
        }
        return null;
    }

    /**
     * method that calls Cleanup to rollback a connection, even if there is
     * an exception.
     * The other will call Cleanup and enable autocommit to the connection.
     */

    private void transactionFailed(){
        Cleanup.performRollback(connection);
        Cleanup.enableAutoCommit(connection);
    }


}
