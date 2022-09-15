package elecdisjunct.repo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * try (TemplateDAO dao = new TemplateDAO()) {
 *      dao.doStuff();
 * } catch (SQLException e) {
 *      // handle dat error
 *      e.printStackTrace(); // or something
 * }
 *
 * @author Tore Bergebakken
 */

class TemplateDAO implements AutoCloseable {

    private Connection connection;

    TemplateDAO() throws SQLException {

        connection = Database.getInstance().getConnection();
    }

    Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {

            connection.close();
        }
    }


}
