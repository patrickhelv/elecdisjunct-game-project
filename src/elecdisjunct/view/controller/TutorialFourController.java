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

public class TutorialFourController implements Stageable {
    private Stage stage;

    @FXML
    public Label sabotage, upgrade;

    @FXML
    private ImageView sabotage1, sabotage2, sabotage3, upgrade1, upgrade2;

    public void initialize(){
        sabotage1.setImage(new Image(new File("resources/Sabotage1.png").toURI().toString()));
        sabotage2.setImage(new Image(new File("resources/Sabotage2.png").toURI().toString()));
        sabotage3.setImage(new Image(new File("resources/Sabotage3.png").toURI().toString()));
        upgrade1.setImage(new Image(new File("resources/Upgrade1.png").toURI().toString()));
        upgrade2.setImage(new Image(new File("resources/Upgrade2.png").toURI().toString()));

        sabotage.setText("Sabotage:\n\nAfter round 17 all players have the opportunity to sabotage one of its opponents nodes once." +
                "\n\nThis can be done by:\n\n1. Select the node you want to be sabotaged\n\n2. Click the sabotage button.\n\n3. See that the node has been " +
                "sabotaged by the big cross on top of the node.");

        upgrade.setText("Upgrade:\n\nAnother option in the game other than claiming nodes and lines, is to upgrade nodes you already own." +
                "\n\nThis can be done in the following way:\n\n1. Select the node you want to upgrade.\n\n2. Click the upgrade button to upgrade.");
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWTHREE).getScene());
    }

    @FXML
    private void handleNext(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWFIVE).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
