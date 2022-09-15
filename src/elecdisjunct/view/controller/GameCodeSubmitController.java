
package elecdisjunct.view.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import elecdisjunct.data.game.Lobby;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the gamecode submit for the fxml file
 *
 * @author patrick Helvik Legendre
 */

public class GameCodeSubmitController implements Stageable {
    private Stage stage;
    static int gamecode;
    private Lobby lobby;

    @FXML
    private JFXButton submitButton;

    @FXML
    private Label NicknamePlaceholder, ErrorMsg;

    @FXML
    private Hyperlink MainMenu;

    @FXML
    private JFXTextField CodeField;

    /**
     *
     * This method will handle everything when a player
     * clicks on the button submit.
     *
     * It will first check the textfield annd if the gamecode
     * exists it will transfer the player to the lobby scene with
     * that gamecode.
     *
     */

    @FXML
    public void handleSubmit(){
        submitButton.setDisable(true);
        String input = CodeField.getText();
        boolean ok = false;
        if(input == null){
            ErrorMsg.setText("Nothing entered in CodeField");
        }else {
            try {
                gamecode = Integer.parseInt(input.trim());
            } catch (Exception e) {
                ErrorMsg.setText("Input Invalid");
            }
            if (gamecode != 0 && gamecode > 0) { // checks the gamecode
                lobby = new Lobby();
                ok = lobby.VerifyGameCode(gamecode); //it will verify if the code exists in the database
                System.out.println(gamecode);
                if (ok == false) {
                    ErrorMsg.setText("Invalid game code");
                    submitButton.setDisable(false);
                } else {
                    LobbyController.setLobby(lobby);
                    stage.setScene(MainApp.getScenes().get(SceneName.LOBBY).getScene());
                }
            }else{
                ErrorMsg.setText("Invalid game code");
                submitButton.setDisable(false);
            }
        }
    }

    /**
     * method that handles when a player clicks the main menu button
     */

    @FXML
    public void handleMainMenu(){
       stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    /**
     * The initialize method will display the nickname of the player.
     */

    @FXML
    public void initialize(){
        System.out.println(UserHandler.getUser().getNickname());
        NicknamePlaceholder.setText(UserHandler.getUser().getNickname());
    }

    @Override
    public void setStage(Stage stage){
        this.stage = stage;}

}
