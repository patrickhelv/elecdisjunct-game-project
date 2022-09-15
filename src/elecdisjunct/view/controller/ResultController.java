package elecdisjunct.view.controller;

import elecdisjunct.data.game.MatchAbstraction;
import elecdisjunct.data.game.Player;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 *
 * @author Tore Bergebakken
 * @author Mia Fornes
 */

public class ResultController implements Stageable {

    private Stage stage;

    private static Player[] players;

    @FXML
    private TableView<Player> tableView;

    @FXML
    private TableColumn<ObservableList<Player>, String> playerColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn, nodeColumn, lineColumn;


    public static void setPlayers(Player[] themPlayers) {
        players = themPlayers;
    } // only this is necessary...

    @Override
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void initialize() {

        generateResultTable();

    }

    private void generateResultTable() {

        ObservableList<Player> playerObservableList = FXCollections.observableArrayList(players); // fixed now

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        nodeColumn.setCellValueFactory(new PropertyValueFactory<>("nodes"));
        lineColumn.setCellValueFactory(new PropertyValueFactory<>("lines"));

        playerColumn.setCellFactory(e -> new TableCell<ObservableList<Player>, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty) {
                    for(Player p : players) {
                        if(item.equals(p.getNickname())) {
                            this.setText(p.getNickname() + (p.isPresent() ? "" : " (left)"));
                            this.setTextFill(p.getColor().getPaint());
                            this.setStyle("-fx-font-weight: bold");
                        }
                    }
                }
            }
        });

        tableView.setItems(playerObservableList);

    }

    @FXML
    public void handleMainMenu() {
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

}
