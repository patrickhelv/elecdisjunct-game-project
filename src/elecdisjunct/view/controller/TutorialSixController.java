package elecdisjunct.view.controller;

import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Victoria Blichfeldt
 */

public class TutorialSixController implements Stageable {
    private Stage stage;

    @FXML
    private ImageView image1, image2;

    @FXML
    private Label scoreboard, round, turn, yourScore;

    public void initialize(){
        image1.setImage(new Image(new File("resources/scoreboard.png").toURI().toString()));
        image2.setImage(new Image(new File("resources/scoreoverview.png").toURI().toString()));

        scoreboard.setText("The scoreboard lets you know every players score, who's in the lead and how many nodes and lines each player has claimed.");
        round.setText("Keep track of how many rounds have been played");
        turn.setText("See your current score");
        yourScore.setText("View whose turn it is");
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWFIVE).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
