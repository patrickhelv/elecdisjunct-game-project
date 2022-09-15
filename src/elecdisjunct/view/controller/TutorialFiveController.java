package elecdisjunct.view.controller;

import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @author Tore Bergebakken
 * @author Victoria Blichfeldt
 */

public class TutorialFiveController implements Stageable {
    private Stage stage;

    @FXML
    private Label infoLabel;

    public void initialize() {

        infoLabel.setText(
                "Each player's score is a total accumulated during the currently elapsed rounds of the match.\n\n" +
                        "The total amount of electricity flowing through all the lines a player has claimed " +
                        "is added to that player's score at the end of each round.\n\n" +
                        "This throughput is calculated as the sum of the output of the two power plants at each end of the line, " +
                        "not counting unclaimed ones and diminishing the output from ones that are owned by another player."
        );
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleNext(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWSIX).getScene());
    }

    @FXML
    private void handleBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWFOUR).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
