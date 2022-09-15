package elecdisjunct.repo;

import elecdisjunct.data.game.DummyFactory;
import elecdisjunct.data.game.Node;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Tore Bergebakken
 * @author Mia Fornes
 */
public class NodeDAO extends TemplateDAO {

    public NodeDAO() throws SQLException {
    }

    public boolean insertMultiple(int matchID, Node[] nodes) throws SQLException {

        System.out.println("inserting with matchID " + matchID);

        String sqlInsert = "INSERT INTO node(match_id, pos_x, pos_y, type, node.level/*, node.owner*/) VALUES (?, ?, ?, ?, ?/*, ?*/)";

        int res = 0;

        getConnection().setAutoCommit(false);

        Savepoint savepoint = getConnection().setSavepoint("Before Node insertion");

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlInsert)) {

            for (Node node : nodes) {

                preparedStatement.setInt(1, matchID);

                preparedStatement.setInt(2, node.getPosX());
                preparedStatement.setInt(3, node.getPosY());
                preparedStatement.setString(4, node.getEnumName() /*node.getType().split(" ")[0].toLowerCase()*/); // particular way of fetching the string, very specific

                preparedStatement.setInt(5, node.getLevel());
                /*
                if (node.getOwner() == null) {
                    System.out.println("before the null");
                    preparedStatement.setNull(6, Types.INTEGER);
                    System.out.println("after the null");
                } else {
                    System.out.println("before the owner");
                    preparedStatement.setInt(6, node.getOwner().getUserID());
                    System.out.println("the problematic userID: " + node.getOwner().getUserID());
                }
                */
                res = preparedStatement.executeUpdate();

                if (res != 1) break; // break out of loop, keeping res value
            }

            if (res == 1) {
                System.out.println("committing...");
                getConnection().commit();
            } else {
                System.out.println("rolling back");
                getConnection().rollback(savepoint);
            }

        } catch (SQLException e) {
            getConnection().rollback(savepoint);
            Logger.getLogger(NodeDAO.class.getName()).log(Level.SEVERE, "Inserting all nodes failed", e);
        } finally {
            Cleanup.enableAutoCommit(getConnection());
        }

        return res == 1;
    }

    public boolean performUpdate(int matchID, Node updatedNode, int round, int turn) {

        String sqlUpdate = "UPDATE node SET node.level = ?, broken = ?, node.owner = ?, node.updated_round = ?, node.updated_turn = ? " +
                           "WHERE match_id = ? AND pos_x = ? AND pos_y = ?;";
        int res = 0;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlUpdate)) {

            preparedStatement.setInt(1, updatedNode.getLevel());
            preparedStatement.setBoolean(2, updatedNode.isBroken());
            if (updatedNode.getOwner() == null) {
                preparedStatement.setNull(3, Types.INTEGER);
            } else {
                preparedStatement.setInt(3, updatedNode.getOwner().getUserID());
            }

            preparedStatement.setInt(4, round);
            preparedStatement.setInt(5, turn);

            preparedStatement.setInt(6, matchID);
            preparedStatement.setInt(7, updatedNode.getPosX());
            preparedStatement.setInt(8, updatedNode.getPosY());

            res = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Logger.getLogger(NodeDAO.class.getName()).log(Level.WARNING, "Could not perform Node update", e);
        }
        return res == 1;
    }

    /**
     * This method may create an awful lot of throwaway objects, but normally it should <i>not</i>.
     *
     * @param matchID    ID of the ongoing match
     * @param lastRound  last round with known state
     * @param lastTurn   last turn with known state
     * @return           an ArrayList with the updated nodes
     */
    public ArrayList<Node> fetchUpdates(int matchID, int lastRound, int lastTurn) {

        String sqlFetch = "SELECT pos_x, pos_y, node.level, broken, node.owner FROM node " +
                          "WHERE match_id = ? AND (updated_round >= ? OR (updated_round = ? AND updated_turn >= ?));";

        ArrayList<Node> updatedNodes = new ArrayList<>();

        try (PreparedStatement fetchStatement = getConnection().prepareStatement(sqlFetch)) {

            fetchStatement.setInt(1, matchID);
            fetchStatement.setInt(2, lastRound);
            fetchStatement.setInt(3, lastRound);
            fetchStatement.setInt(4, lastTurn);

            try (ResultSet res = fetchStatement.executeQuery()) {

                while (res.next()) {

                    // setting up a dummy object

                    updatedNodes.add(DummyFactory.createDummyNode(res.getInt("pos_x"), res.getInt("pos_y"),
                                                                  res.getInt("level"), res.getBoolean("broken"),
                                                                  res.getInt("owner")));
                }

            } catch (SQLException sqle) {
                Logger.getLogger(NodeDAO.class.getName()).log(Level.WARNING, "Parsing ResultSet failed while fetching Node updates", sqle);
            }

        } catch (SQLException sqle) {
            Logger.getLogger(NodeDAO.class.getName()).log(Level.WARNING, "Could not fetch Node updates", sqle);
        }

        return updatedNodes;

    }

}
