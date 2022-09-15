package elecdisjunct.view.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.data.util.Validator;
import elecdisjunct.repo.UserDAO;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * @author Victoria Blichfeldt
 */

public class LoginController implements Stageable {
    private Stage stage;

    @FXML
    private Label errorMessage;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXTextField email;

    /**
     * Method that handles login when login button is clicked
     * If login successful, transfers the user to the main.
     * If not successful login, shows error message.
     */

    @FXML
    private void handleLogin() {
        if (isInputValid()) {
            try (UserDAO userDAO = new UserDAO()) {
                if (userDAO.isPasswordCorrect(email.getText().trim().toLowerCase(), password.getText().trim().toCharArray())) {
                    if(userDAO.logIn(email.getText().trim().toLowerCase())) {
                        UserHandler.setUser(userDAO.getUser(email.getText().trim().toLowerCase()));
                        email.clear();
                        password.clear();
                        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
                    }else{
                        errorMessage.setText("Already logged in");
                        errorMessage.setVisible(true);
                        password.clear();
                        email.clear();
                    }
                } else {
                    errorMessage.setVisible(true);
                    password.clear();
                    email.clear();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleGoBack() {
        stage.setScene(MainApp.getScenes().get(SceneName.WELCOME_VIEW).getScene());
    }

    /**
     * Method that checks if input is valid
     * @return true if input is valid, false if not
     */
    private boolean isInputValid(){
        if(!Validator.isText(email.getText().trim())){
            errorMessage.setVisible(true);
            password.clear();
            email.clear();
            return false;
        }else if(!Validator.isText(password.getText().trim())){
            errorMessage.setVisible(true);
            password.clear();
            email.clear();
            return false;
        }
        return true;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
