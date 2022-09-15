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

import java.io.IOException;

/**
 * @author Victoria Blichfeldt
 */

public class UserRegisterController implements Stageable {
    private Stage stage;

    @FXML
    private JFXPasswordField password, reentered;

    @FXML
    private JFXTextField email, nickname;

    @FXML
    private Label errorMessage;

    /**
     *  Handles user registering
     */

    @FXML
    public void handleRegisterUser(){
        if(!isInputValid()){
            errorMessage.setText("");
            try(UserDAO userDAO = new UserDAO()){
                if(userDAO.addUser(email.getText().trim().toLowerCase(), nickname.getText().trim(), password.getText().trim().toCharArray())) {
                    if(userDAO.logIn(email.getText().trim().toLowerCase())) {
                        UserHandler.setUser(userDAO.getUser(email.getText().trim().toLowerCase()));
                        email.clear();
                        password.clear();
                        reentered.clear();
                        nickname.clear();
                        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
                    }else{
                        errorMessage.setText("Something went wrong registering");
                        errorMessage.setVisible(true);
                        email.clear();
                        password.clear();
                        reentered.clear();
                        nickname.clear();
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleGoBack() throws IOException {
        password.clear();
        reentered.clear();
        email.clear();
        nickname.clear();
        errorMessage.setVisible(false);
        stage.setScene(MainApp.getScenes().get(SceneName.WELCOME_VIEW).getScene());
    }

    /**
     * Method that checks if input is valid
     * @return true if input is valid, false if not
     */
    private boolean isInputValid(){
        String error = "Error! ";
        String p = password.getText().trim();
        String n = nickname.getText().trim();
        String e = email.getText().trim();
        String r = reentered.getText().trim();

        boolean errorOccured = false;
        if (!Validator.isText(e)) {
            error += "Invalid email, ";
            errorOccured = true;
        }else if(!e.contains("@") || !e.contains(".")){
            error += "Invalid email, ";
            errorOccured = true;
        }


        if(!Validator.isPasswordEqual(p, r)){
            error += "Passwords does not match, ";
            errorOccured = true;
        }
        if(!Validator.isPasswordTooShort(p)){
            error += "Password too short, ";
            errorOccured = true;
         }
        if(!Validator.isPasswordTooLong(p)){
            error += "Password too long, ";
            errorOccured = true;
        }

        if(!Validator.isNicknameTooLong(n)){
            error += "Nickname to long, maximum 20 characters";
            errorOccured = true;
        }else if(!Validator.isText(n)){
            error += "Invalid nickname, ";
            errorOccured = true;
        }

        if(!errorOccured){//check if works
            if(Validator.isEmailRegistered(e)){
                error += "Email already registered, ";
                errorOccured = true;
            }
        }


        if(errorOccured){
            errorMessage.setText(error);
        }else{
            errorMessage.setText("");
        }

        return Validator.isText(errorMessage.getText().trim());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
