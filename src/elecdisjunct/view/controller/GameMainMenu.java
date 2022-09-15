package elecdisjunct.view.controller;

import com.jfoenix.controls.JFXSlider;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.data.util.Music;
import elecdisjunct.repo.UserDAO;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;

/**
 * @author Victoria Blichfeldt
 */

public class GameMainMenu implements Stageable {
    private Stage stage;

    @FXML
    private AnchorPane soundSet;

    @FXML
    private Label userlog;

    @FXML
    private JFXSlider volume;

    @FXML
    private Rectangle soundButton;

    public void initialize(){
        menuSlideAnimation();
        //String soundicon = "audio-volume.png";
        soundButton.setFill(new ImagePattern(new Image(
                new File(("resources/audio-volume.png")/*.concat(soundicon)*/).toURI().toString()
        )));
    }

    @FXML
    private void handlePlayGame(){
        stage.setScene(MainApp.getScenes().get(SceneName.PLAY_GAME).getScene());
    }

    @FXML
    private void handleTutorial(){
        stage.setScene(MainApp.getScenes().get(SceneName.TUTORIALVIEWONE).getScene());
    }

    @FXML
    public void handleCredits(){
        stage.setScene(MainApp.getScenes().get(SceneName.CREDITS).getScene());
    }

    @FXML
    private void handlePersonalStats(){
        stage.setScene(MainApp.getScenes().get(SceneName.PERSONAL_STATS).getScene());
    }

    @FXML
    private void handleGlobalStats(){
        stage.setScene(MainApp.getScenes().get(SceneName.GLOBAL_STATS).getScene());
    }

    @FXML
    private void editinfo() { stage.setScene(MainApp.getScenes().get(SceneName.EDIT_INFO).getScene()); }

    @FXML
    private void volumeSetting(){
        Music.setVolume(volume.getValue()/100);
    }

    @FXML
    private void handleLogout(){
        try (UserDAO userDAO = new UserDAO()){
            if (userDAO.logOut(UserHandler.getUser().getEmail())){
                UserHandler.setUser(null);
                stage.setScene(MainApp.getScenes().get(SceneName.WELCOME_VIEW).getScene());
            }else{
                //TODO message to say no able to log out - something like this might work?
                Alert alert = new Alert(Alert.AlertType.WARNING, "Not able to log out!",
                        ButtonType.OK);
                ButtonType result = alert.showAndWait().orElse(ButtonType.OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void menuSlideAnimation(){
        TranslateTransition openMenu = new TranslateTransition(new Duration(750), soundSet);
        openMenu.setToX(190);
        TranslateTransition closeMenu = new TranslateTransition(new Duration(1000), soundSet);
        soundButton.setOnMouseClicked(e -> {
            if (soundSet.getTranslateX()!=190) {
                openMenu.play();
            } else {
                closeMenu.setToX((soundSet.getWidth())*1.3);
                closeMenu.play();
            }
        });
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        userlog.setText("Logged in as: " + UserHandler.getUser().getNickname());
    }
}
