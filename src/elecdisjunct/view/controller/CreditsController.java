package elecdisjunct.view.controller;

import com.sun.javafx.PlatformUtil;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;


/**
 * The controller for the credits scene
 *
 * @author patrick Helvik Legendre
 * @author Victoria Blichfeldt
 */

public class CreditsController implements Stageable {
    private Stage stage;
    private boolean ok = java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) && !PlatformUtil.isLinux();

    private String url1 = "https://www.flaticon.com/authors/silviu-runceanu";
    private String url2 = "https://www.flaticon.com/";
    private String url3 = "http://creativecommons.org/licenses/by/3.0/";
    private String url4 = "https://incompetech.com/";
    private String url5 = "https://github.com/ksnortum/javafx-multi-scene-fxml";
    private String url6 = "https://www.freepik.com/";
    private String url7 = "https://www.flaticon.com/authors/those-icons";

    @FXML
    Text teamMembers;

    @FXML
    public void handleMainMenu(){
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    //opens the url to the website
    @FXML
    public void handleLink1(){
        if(ok) {
            openUrl(url1);
        }
    }

    @FXML
    public void handleLink2(){
        if(ok){
            openUrl(url2);
        }
    }

    @FXML
    public void handleLink3(){
        if(ok){
            openUrl(url3);
        }
    }

    @FXML
    public void handleLink4(){
        if(ok){
            openUrl(url4);
        }
    }

    @FXML
    public void handleLink5(){
        if (ok){
            openUrl(url5);
        }
    }

    @FXML
    public void handleLink6(){
        if(ok){
            openUrl(url6);
        }
    }

    @FXML
    public void handleLink7(){
        if(ok){
            openUrl(url7);
        }
    }


    private void openUrl(String text){ //method to open the hypertext
        try {
            java.net.URI url = new java.net.URI(text);
            java.awt.Desktop.getDesktop().browse(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * sets a new stage and displays the text roll
     * animation.
     *
     * @param stage
     */


    @Override
    public void setStage(Stage stage){
        this.stage = stage;

        KeyValue keyValue = new KeyValue(teamMembers.yProperty(), stage.getHeight());
        KeyFrame keyFrame = new KeyFrame(Duration.ZERO, keyValue);

        KeyValue end = new KeyValue(teamMembers.translateYProperty(), stage.getHeight());
        KeyFrame endframe = new KeyFrame(Duration.seconds(9), end);

        Timeline timeline = new Timeline(keyFrame, endframe);
        timeline.setCycleCount(timeline.INDEFINITE);
        timeline.play();
    }

}
