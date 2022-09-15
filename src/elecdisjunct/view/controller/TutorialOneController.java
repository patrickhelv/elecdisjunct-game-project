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
 *
 * @author Victoria Blichfeldt
 */

public class TutorialOneController implements Stageable {
    private Stage stage;

    @FXML
    private Label mapText;

    @FXML
    private ImageView map;

    public void initialize(){
        map.setImage(new Image(new File("resources/map.png").toURI().toString()));
        mapText.setText("Welcome to Electric Disjunction!\n\nIn this game you will be playing a power magnate competing against other players to acquire " +
                "as much power as possible." +
                "\nAll the players start in each their corner of the map represented by the same color as their" +
                "name on the scoreboard. All the power plants and parks are connected by lines. To be able to claim a power plant " +
                "one need to already own a connecting line. The game will have a total of 26 rounds and the player with the most points when last round " +
                "has passed, wins the game.\n\nLet the fun begin!");
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleNext(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWTWO).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
