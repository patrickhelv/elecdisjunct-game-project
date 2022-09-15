package elecdisjunct.repo;

import elecdisjunct.data.game.DummyFactory;
import elecdisjunct.data.game.Line;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tore Bergebakken
 */
public class LineDAO extends TemplateDAO {

    public LineDAO() throws SQLException {}

    public boolean insertMultiple(int matchID, Line[] lines) throws SQLException {

        String sqlInsert = "INSERT INTO line(match_id, from_x, from_y, to_x, to_y, line.level/*, line.owner*/) VALUES (?, ?, ?, ?, ?, ?/*, ?*/)";

        int res = 0;

        getConnection().setAutoCommit(false);

        Savepoint savepoint = getConnection().setSavepoint("Before Node insertion");

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlInsert)) {

            for (Line line : lines) {

                preparedStatement.setInt(1, matchID);

                preparedStatement.setInt(2, line.getFrom().getPosX());
                preparedStatement.setInt(3, line.getFrom().getPosY());
                preparedStatement.setInt(4, line.getTo().getPosX());
                preparedStatement.setInt(5, line.getTo().getPosY());

                preparedStatement.setInt(6, line.getLevel());
                /*
                if (line.getOwner() == null) {
                    preparedStatement.setNull(7, Types.INTEGER);
                } else {
                    preparedStatement.setInt(7, line.getOwner().getUserID());
                }
                */
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
            Logger.getLogger(LineDAO.class.getName()).log(Level.SEVERE, "Inserting all nodes failed", e);
        } finally {
            Cleanup.enableAutoCommit(getConnection());
        }

        return res == 1;
    }

    public boolean performUpdate(int matchID, Line updatedLine, int round, int turn) {

        String sqlUpdate = "UPDATE line SET line.level = ?, broken = ?, line.owner = ?, updated_round = ?, updated_turn = ? " +
                           "WHERE match_id = ? AND from_x = ? AND from_y = ? AND to_x = ? AND to_y = ?;";
        int res = 0;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlUpdate)) {

            preparedStatement.setInt(1, updatedLine.getLevel());
            preparedStatement.setBoolean(2, updatedLine.isBroken());
            if (updatedLine.getOwner() == null) {
                preparedStatement.setNull(3, Types.INTEGER);
            } else {
                preparedStatement.setInt(3, updatedLine.getOwner().getUserID());
            }

            preparedStatement.setInt(4, round);
            preparedStatement.setInt(5, turn);

            preparedStatement.setInt(6, matchID);
            preparedStatement.setInt(7, updatedLine.getFrom().getPosX());
            preparedStatement.setInt(8, updatedLine.getFrom().getPosY());
            preparedStatement.setInt(9, updatedLine.getTo().getPosX());
            preparedStatement.setInt(10, updatedLine.getTo().getPosY());

            res = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Logger.getLogger(LineDAO.class.getName()).log(Level.WARNING, "Could not perform Line update", e);
        }
        return res == 1;
    }

    /**
     * This method may create an awful lot of throwaway objects, but normally it should <i>not</i>.
     *
     * @param matchID    ID of the ongoing match
     * @param lastRound  last round with known state
     * @param lastTurn   last turn with known state
     * @return           an ArrayList with the updated lines
     */
    public ArrayList<Line> fetchUpdates(int matchID, int lastRound, int lastTurn) {

        String sqlFetch = "SELECT from_x, from_y, to_x, to_y, line.level, broken, line.owner FROM line " +
                          "WHERE match_id = ? AND (updated_round >= ? OR (updated_round = ? AND updated_turn >= ?));";

        ArrayList<Line> updatedLines = new ArrayList<>();

        try (PreparedStatement fetchStatement = getConnection().prepareStatement(sqlFetch)) {

            fetchStatement.setInt(1, matchID);
            fetchStatement.setInt(2, lastRound);
            fetchStatement.setInt(3, lastRound);
            fetchStatement.setInt(4, lastTurn);

            try (ResultSet res = fetchStatement.executeQuery()) {

                while (res.next()) {

                    // setting up a dummy object
                    updatedLines.add(DummyFactory.createDummyLine(res.getInt("from_x"), res.getInt("from_y"),
                                                                  res.getInt("to_x"), res.getInt("to_y"), // OH MY GOD
                                                                  res.getInt("level"), res.getBoolean("broken"),
                                                                  res.getInt("owner")));
                }

            } catch (SQLException sqle) {
                Logger.getLogger(LineDAO.class.getName()).log(Level.WARNING, "Parsing ResultSet failed while fetching Line updates", sqle);
            }

        } catch (SQLException sqle) {
            Logger.getLogger(LineDAO.class.getName()).log(Level.WARNING, "Could not fetch Line updates", sqle);
        }

        return updatedLines;

    }

}
