package elecdisjunct.view.controller;

import elecdisjunct.data.user.UserHandler;
import elecdisjunct.repo.StatsDAO;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Mia Fornes
 */

public class PersonalStatisticsController implements Stageable {

    private Stage stage;

    @FXML
    private Label highScore, gamesPlayed, gamesWon, nameLabel;

    @FXML
    private Hyperlink mainMenu;

    @FXML
    public void initialize() {

        nameLabel.setText(UserHandler.getUser().getNickname()); // why upper case? why?

        try (StatsDAO statsDAO = new StatsDAO()){
            highScore.setText(statsDAO.getHighscore(UserHandler.getUser().getUserId()));
            gamesPlayed.setText(statsDAO.getGamesPlayed(UserHandler.getUser().getUserId()));
            gamesWon.setText(statsDAO.getGamesWon(UserHandler.getUser().getUserId()));
        } catch (SQLException e) {
            Logger.getLogger(PersonalStatisticsController.class.getName()).log(Level.WARNING, "Could not load statistics", e);
        }

    }

    @FXML
    public void handleMainMenu() {
        stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
