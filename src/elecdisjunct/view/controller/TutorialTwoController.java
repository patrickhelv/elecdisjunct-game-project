package elecdisjunct.view.controller;

import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author Victoria Blichfeldt
 */

public class TutorialTwoController implements Stageable {
    private Stage stage;

    @FXML
    private Circle city, coal, gas, hydro, nuclear, solar, wind;

    @FXML
    private Label cityText, coalText, gasText, hydroText, nuclearText, solarText, windText, renewable, nonrenewable, info;

    public void initialize(){
        city.setFill(new ImagePattern(new Image(new File("resources/city.JPG").toURI().toString())));
        cityText.setText("Cities don't produce electricity, they consume it\n- but in this game they don't impact negatively on your score." +
                "Be careful as they cannot be claimed and thus may slow you down on your way to greater energy sources");

        coal.setFill(new ImagePattern(new Image(new File("resources/coal.JPG").toURI().toString())));
        coalText.setText("Coal Power Plants");

        gas.setFill(new ImagePattern(new Image(new File("resources/gas.JPG").toURI().toString())));
        gasText.setText("Gas Power Plants");

        hydro.setFill(new ImagePattern(new Image(new File("resources/hydroelectric.JPG").toURI().toString())));
        hydroText.setText("Hydroelectric Power Plants");

        nuclear.setFill(new ImagePattern(new Image(new File("resources/nuclear.JPG").toURI().toString())));
        nuclearText.setText("Nuclear Power Plants:\nGive the highest output but are only present in\na highly contested area in the middle of the map");

        solar.setFill(new ImagePattern(new Image(new File("resources/solar.JPG").toURI().toString())));
        solarText.setText("Solar Parks");

        wind.setFill(new ImagePattern(new Image(new File("resources/windmill.JPG").toURI().toString())));
        windText.setText("Windmill Parks");

        renewable.setText(
                "Renewable power plants don't give the best output in the beginning,\n" +
                "but will increase as you level them up");

        nonrenewable.setText(
                "Non-renewable power plants, using fossil fuels as their energy source,\n" +
                "have relatively high output at lower levels, but it doesn't increase as\n" +
                "much as the renewable plants when leveling up");

        info.setText("Power plants can be upgraded to give higher outputs and increase your chance of winning.");
    }

    @FXML
    private void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @FXML
    private void handleNext(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWTHREE).getScene());
    }

    @FXML
    private void handleBack(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWONE).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
