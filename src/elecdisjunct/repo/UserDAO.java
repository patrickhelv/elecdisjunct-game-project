package elecdisjunct.repo;

import elecdisjunct.data.user.User;
import elecdisjunct.data.util.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Data Access Object for User
 *
 * @author Victoria Blichfeldt
 */

public class UserDAO extends TemplateDAO{
    private Connection connection;


    public UserDAO() throws SQLException{
        this.connection = super.getConnection();
    }

    /**
     * Set user as logged in database
     *
     * @param email users email
     * @return true if log in is successful, false if not
     */
    public boolean logIn(String email) throws SQLException{
        //add in check to see if user is already logged in
        String sqlSentence1 = "SELECT logged_in FROM users WHERE email = ?";
        String sqlSentence2 = "UPDATE users SET logged_in = '1' WHERE email = ?";
        boolean resultBoolean = false;
        ResultSet resultSet;
        int result = 0;

        try(PreparedStatement preparedStatement1 = connection.prepareStatement(sqlSentence1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sqlSentence2)){
            connection.setAutoCommit(false);
            preparedStatement1.setString(1, email);
            resultSet = preparedStatement1.executeQuery();
            if(resultSet.next()){
                resultBoolean = resultSet.getBoolean("logged_in");
            }
            if(!resultBoolean) {
                preparedStatement2.setString(1, email);
                result = preparedStatement2.executeUpdate();
                connection.commit();
            }
        }catch (SQLException e){
            Cleanup.performRollback(connection);
        }finally {
            Cleanup.enableAutoCommit(connection);
        }
        return result == 1;
    }

    /**
     * Set user a logged out in database
     *
     * @param email users email
     * @return true if log out is successful, false if not
     */
    public boolean logOut(String email) throws SQLException { //TODO change to use ID not email
        String sqlSentence = "UPDATE users SET logged_in = '0' WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence)){
            preparedStatement.setString(1, email);
            int result = preparedStatement.executeUpdate();
            return result == 1;
        }
    }

    /**
     * Method that gives access to a User object, given an email
     *
     * @param email for the User object wanted access for
     * @return User object with the given email
     * @throws SQLException if failing to get user
     */
    public User getUser(String email) throws SQLException{ //TODO change to use ID not email
        String sqlSentence = "SELECT user_id, email, nickname FROM users WHERE email = ?";
        ResultSet resultSet = null;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence)){
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            User u = null;
            if(resultSet.next()){
                u = new User();
                u.setUserId(resultSet.getInt("user_id"));
                u.setEmail(resultSet.getString("email"));
                u.setNickname(resultSet.getString("nickname"));
            }
            return u;
        }finally{
            if(resultSet != null){resultSet.close();}
        }
    }

    /**
     * Adds a user to the database given email, nickname and password
     *
     * @param email for the user
     * @param nickname for the user
     * @param password for the user
     * @return true if user is successfully added, false if not
     * @throws SQLException if failing to add user
     */
    public boolean addUser(String email, String nickname, char[] password) throws  SQLException{
        String sqlSentence = "INSERT INTO users (email, password, salt, nickname) VALUES(?,?,?,?);";

        byte[] salt = Password.getSalt();
        byte[] hashedPassword = Password.hash(password, salt);

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence)){
            preparedStatement.setString(1, email);
            preparedStatement.setBytes(2, hashedPassword);
            preparedStatement.setBytes(3, salt);
            preparedStatement.setString(4, nickname);
            int result = preparedStatement.executeUpdate();
            Arrays.fill(password, 'a');
            return result == 1;
        }
    }

    /**
     * Updates user with the same user id as User object gives
     *
     * @param u user with new and updated information
     * @return true if successsfully updated
     * @throws SQLException if query fails
     */
    public boolean updateNickname(User u, String newNickname) throws SQLException{
        String sqlStatemente = "UPDATE users SET nickname = ? WHERE user_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlStatemente)){
            preparedStatement.setString(1, newNickname);
            preparedStatement.setInt(2, u.getUserId());
            int res = preparedStatement.executeUpdate();
            return res == 1;
        }
    }

    /**
     * Sets a password for a user given user id
     *
     * @param user_id of the user who's password to be changed
     * @param password should be the new password for the user
     * @return true if the new password is successfully set
     * @throws SQLException if setting new password fails
     */
    public boolean setPassword(int user_id, char[] password) throws SQLException{
        byte[] salt = Password.getSalt();
        String sqlSentence2 = "UPDATE users SET password = ? , salt = ? WHERE user_id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence2)){
            preparedStatement.setBytes(1, Password.hash(password, salt));
            preparedStatement.setBytes(2, salt);
            preparedStatement.setInt(3, user_id);
            int result = preparedStatement.executeUpdate();
            Arrays.fill(password, 'a');
            return result == 1;
        }
    }

    /**
     * Checks if given password is correct for a user, given email
     *
     * @param email of the user for the password to be checked
     * @param password to be checked if it is correct
     * @return true if password is correct, false if not
     * @throws SQLException if query fails
     */
    public boolean isPasswordCorrect(String email, char[] password) throws SQLException{
        byte[] salt;
        byte[] hashed;
        ResultSet resultSet = null;
        String sqlSentence = "SELECT salt, password FROM users WHERE email = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence)){
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                salt = resultSet.getBytes("salt");
                hashed = resultSet.getBytes("password");
                return Password.verify(password, hashed, salt);
            }

        }finally {
            if (resultSet != null){
                resultSet.close();
            }
        }
        Arrays.fill(password, 'a');
        return false;
    }
}