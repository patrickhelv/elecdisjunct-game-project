package elecdisjunct.view.controller;

import com.jfoenix.controls.JFXButton;
import elecdisjunct.data.game.Lobby;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

/**
 *
 * @author Patrick Helvik Legendre
 */


public class PlayGameOptionsController implements Stageable {
    private Stage stage;
    private static Lobby lobby;

    @FXML
    JFXButton createGameButton, joinGameButton;

    @FXML
    Hyperlink goBack;

    @FXML
    public void createGame() {
        lobby = new Lobby(UserHandler.getUser().getUserId());
        LobbyController.setLobby(lobby);
        stage.setScene(MainApp.getScenes().get(SceneName.LOBBY).getScene());
    }

    @FXML
    public void joinGame() {
        stage.setScene(MainApp.getScenes().get(SceneName.JOIN_LOBBY).getScene());
    }

    @FXML
    public void handlegoBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
