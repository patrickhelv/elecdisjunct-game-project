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


public class TutorialThreeController implements Stageable {
    private Stage stage;

    @FXML
    private Label claimText;

    @FXML
    private ImageView image1, image2, image3;

    public void initialize(){
        image1.setImage(new Image(new File("resources/claim1.png").toURI().toString()));
        image2.setImage(new Image(new File("resources/claim2.png").toURI().toString()));
        image3.setImage(new Image(new File("resources/claim3.png").toURI().toString()));

        claimText.setText("To claim a node or a line you can either double click the wanted line or node or you can\n" +
                "\n1. Click on the wanted line or node.\n\n2. Press the claim button.\n\n3. See that you have claimed the node" +
                "when it has turned your color\n\nIt's as easy as that!");
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleNext(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWFOUR).getScene());
    }

    @FXML
    private void handleBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWTWO).getScene());
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
