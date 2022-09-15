package elecdisjunct.repo;

import java.sql.*;
import java.util.logging.Level;

/**
 * Just a class with a bunch of convenient methods used in the DAOs
 *
 * @author Tore Bergebakken
 * @author Patrick Øivind Helvik Legendre
 */
class Cleanup {

    static void enableAutoCommit(Connection connection) {

        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Enabling autocommit failed", e);
            }
        }
    }

    static void performRollback(Connection connection) {

        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Rollback failed", e);
            }
        }

    }

    static void performRollback(Connection connection, Savepoint savepoint) {

        if (connection != null) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e) {
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Rollback failed", e);
            }
        }

    }

    static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Closing ResultSet failed", e);
            }
        }
    }

    static void closeStatement(Statement statement){
        if(statement != null){
            try {
                statement.close();
            }catch (SQLException sq){
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Closing Statement failed", sq);
            }
        }
    }

    static void closePreparedStatement(PreparedStatement preparedStatement){
        if(preparedStatement != null){
            try{
                preparedStatement.close();
            }catch (SQLException sq){
                java.util.logging.Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, "Closing PreparedStatement failed", sq);
            }
        }
    }

}
