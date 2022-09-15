package elecdisjunct.view.controller;

import elecdisjunct.data.game.Player;
import elecdisjunct.repo.StatsDAO;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dave_cz
 * @author Mia Fornes
 */

public class GlobalStatisticsController implements Stageable {

    private Stage stage;

    @FXML
    private TableView<Player> table;

    @FXML
    private TableColumn<Player, Number> rankColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn, nodesColumn, linesColumn;

    @FXML
    private TableColumn<Player, String> nicknameColumn;

    @FXML
    private Hyperlink mainMenu;


    @FXML
    public void initialize() {

        rankColumn.setCellFactory(new LineNumbersCellFactory<>());
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        nodesColumn.setCellValueFactory(new PropertyValueFactory<>("nodes"));
        linesColumn.setCellValueFactory(new PropertyValueFactory<>("lines"));

        try (StatsDAO statsDAO = new StatsDAO()) {
            ObservableList<Player> observableList = statsDAO.getGlobalStats();
            table.setItems(observableList);
        } catch (SQLException e) {
            Logger.getLogger(GlobalStatisticsController.class.getName()).log(Level.WARNING, "Loading statistics failed", e);
        }

    }

    /**
     * Class that is used for numbering the rows in the global statistics table.
     *
     * Written by <a href="https://stackoverflow.com/questions/16384879/auto-numbered-table-rows-javafx">Dave_cz</a>
     * @param <T> The type of the TableView generic type
     * @param <E> The type of the item contained within the cell
     */
    public class LineNumbersCellFactory<T, E> implements Callback<TableColumn<T, E>, TableCell<T, E>> {

        public LineNumbersCellFactory() {
        }

        @Override
        public TableCell<T, E> call(TableColumn<T, E> param) {
            return new TableCell<T, E>() {
                @Override
                protected void updateItem(E item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        setText(this.getTableRow().getIndex() + 1 + "");
                    } else {
                        setText("");
                    }
                }
            };
        }
    }

    @FXML
    public void handleMainMenu() {
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
