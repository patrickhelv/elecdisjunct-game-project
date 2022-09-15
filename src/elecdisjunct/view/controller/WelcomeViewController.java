package elecdisjunct.view.controller;

import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * @author Victoria Blichfeldt
 */

public class WelcomeViewController implements Stageable {
    private Stage stage;

    @FXML
    public void handleRegister(){
    stage.setScene(MainApp.getScenes().get(SceneName.REGISTER).getScene());
    }

    @FXML
    public void handleLogIn(){
        stage.setScene(MainApp.getScenes().get(SceneName.LOGIN).getScene());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
