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

public class UserEditInfoController implements Stageable {
    private Stage stage;

    @FXML
    private JFXPasswordField reenterPassword, oldPassword, newPassword;

    @FXML
    Label wrongPassword, wrongOldPassword, changeCompleteNick, changeCompletePassword, invalidNickname;

    @FXML
    JFXTextField newNickname;

    @FXML
    private void confirmChanges(){
        wrongPassword.setVisible(false);
        wrongOldPassword.setVisible(false);
        invalidNickname.setVisible(false);
        boolean confirmedNickname = false;
        String newN = newNickname.getText().trim();

        if(Validator.isText(newN) && Validator.isNicknameTooLong(newN)){
            try(UserDAO userDAO = new UserDAO()){
                if(userDAO.updateNickname(UserHandler.getUser(), newN)){
                    UserHandler.setUser(userDAO.getUser(UserHandler.getUser().getEmail()));
                    confirmedNickname = true;
                }else{
                    invalidNickname.setVisible(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(confirmedNickname){
            clearFields();
            changeCompleteNick.setVisible(true);
        }

        boolean confirmedPassword = false;
        String newP = newPassword.getText().trim();

        if(!isInputValid()) {
            try (UserDAO userDAO = new UserDAO()) {
                if(userDAO.setPassword(UserHandler.getUser().getUserId(), newP.toCharArray())){
                    confirmedPassword = true;
                }else{
                    wrongPassword.setVisible(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(confirmedPassword){
            clearFields();
            changeCompletePassword.setVisible(true);
        }

    }

    @FXML
    private void setMainMenu() {
        clearFields();
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    private boolean isInputValid(){
        String oldP = oldPassword.getText().trim();
        String newP = newPassword.getText().trim();
        String reenteredP = reenterPassword.getText().trim();

        boolean valid = false;

        if(!Validator.isText(oldP) && !Validator.isText(newP) && !Validator.isText(reenteredP)){
            valid = true;
        }else {
            if (!Validator.isPasswordEqual(newP, reenteredP)) {
                valid = true;
                wrongPassword.setVisible(true);
            }
            if (!Validator.isPasswordTooShort(newP)) {
                valid = true;
                wrongPassword.setText("Password is too short");
                wrongPassword.setVisible(true);
            }
            if (!Validator.isPasswordTooLong(newP)) {
                valid = true;
                wrongPassword.setText("Password is too long");
                wrongPassword.setVisible(true);
            }

            if (!valid) {
                try (UserDAO userDAO = new UserDAO()) {
                    if (!userDAO.isPasswordCorrect(UserHandler.getUser().getEmail(), oldP.toCharArray())) {
                        valid = true;
                        wrongOldPassword.setVisible(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return valid;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void clearFields(){
        newNickname.clear();
        oldPassword.clear();
        reenterPassword.clear();
        newPassword.clear();
        changeCompletePassword.setVisible(false);
        changeCompleteNick.setVisible(false);
        wrongPassword.setVisible(false);
        wrongOldPassword.setVisible(false);
        invalidNickname.setVisible(false);
    }

}
