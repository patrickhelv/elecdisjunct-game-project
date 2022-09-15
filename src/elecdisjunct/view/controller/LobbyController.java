package elecdisjunct.view.controller;

import elecdisjunct.data.game.Lobby;
import elecdisjunct.data.game.MatchUpdater;
import elecdisjunct.data.game.Player;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.data.util.Color;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.util.ArrayList;

import static elecdisjunct.view.controller.GameCodeSubmitController.gamecode;

/**
 * Controller for the lobby.fxml file
 *
 * @author patrick Helvik Legendre
 */


public class LobbyController implements Stageable {
    private Stage stage;
    private static Lobby lobby;
    private ArrayList<Player> players;
    private ObservableList<String> list = FXCollections.observableArrayList();
    private ObservableList<Color> listcoloravailable = FXCollections.observableArrayList();
    private ArrayList<Color> colors;

    private boolean threadShouldRun = true;

    @FXML
    private Label error_msg, gamecodeplaceholder, nicknameplaceholder;

    @FXML
    private ListView<String> playerlistview;

    @FXML
    private Hyperlink mainmenu;

    @FXML
    private ProgressIndicator progression;

    @FXML
    private Button startgame, submitcolor;

    @FXML
    private ChoiceBox colorpicker;

    /**
     * This method is used when
     * a player clicks the button
     * start game in the lobby.
     *
     * It will copy all players in the
     * lobby over to the match game controller.
     */
    @FXML
    public void handleStartGame(){
        if(lobby.isHost()) {
            lobby.UpdateLobby();
            players = lobby.getPlayers();
            System.out.println("initializing start game...");
            if (players.size() > 1 && players.size() < 5) { //it will check if there is enough players to start a game
                startgame.setDisable(true);
                lobby.StartGame(); //call the startgame method in lobby
                Player[] copy = new Player[players.size()];
                for (int i = 0; i < copy.length; i++) {
                    copy[i] = players.get(i); //creates a copy of all players in the lobby
                }
                GameController.setMatch(new MatchUpdater(lobby.getMatchId(), copy, true)); // it will send over the copy of players to the next controller to handle all the players
                threadShouldRun = false;
                stage.setScene(MainApp.getScenes().get(SceneName.ACTUAL_GAME).getScene());
                System.out.println("finished initializing");
            }
        }
        startgame.setDisable(false);
    }

    /**
     * Method that refresh the lobby called
     * by the thread.
     *
     * When players are null this means the host
     * has left the lobby and all players present in lobby
     * will return to main menu and every reference will
     * be updated in the database.
     *
     * If player are not null it will update the observable list for the players
     * present in the lobby. If the player is not a Host it will do an extra
     * check to see if the Host has clicked on the startgame button.
     *
     */

    public void handleRefresh() {
        System.out.println("refresh .....");
        lobby.UpdateLobby(); //call updateLobby method
        players = lobby.getPlayers(); //retrieves players after updateLobby method call

        if(players == null) { //this will happen if the host leaves the lobby
            threadShouldRun = false; //stopping the thread
            lobby.playerLeaves();
            Platform.runLater(() -> stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene())); //sends the players present in lobby to main menu
        }
        for (int i = 0; i < players.size(); i++) {
            System.out.println("nicknames " + players.get(i).getNickname());
            if(UserHandler.getUser().getUserId() == players.get(i).getUserID()){
                nicknameplaceholder.setTextFill(players.get(i).getColor().getPaint());
            }
        }
        Platform.runLater(() -> {
            colors = lobby.colorRemaining();
            listcoloravailable.clear();
            listcoloravailable.addAll(colors); //will add all colors available for players to choose
            list.clear(); //clear observable list
            for (int i = 0; i < players.size(); i++) {
                list.add(players.get(i).getNickname()); //add nickname of all player present in lobby in the observable list
            }
            progression.setProgress(players.size() * 0.25);
            if (!lobby.isHost()) { //if the player is not a host, it will check if the game has started
                if (lobby.checkGameStatus() && players.size() > 1 && players.size() < 5) {
                    Player[] copy = new Player[players.size()];
                    for (int i = 0; i < copy.length; i++) { //if the game has started it will copy all players present in lobby
                        copy[i] = players.get(i);
                    }
                    System.out.println("your game is starting");
                    GameController.setMatch(new MatchUpdater(lobby.getMatchId(), copy)); //send it over to match gamecontroller
                    threadShouldRun = false;
                    stage.setScene(MainApp.getScenes().get(SceneName.ACTUAL_GAME).getScene());
                }
                System.out.println("not enough players or game has not started (or failed to check game status)");
            }
            });


    }

    /**
     *
     * This method is used for players when they choose
     * a new color in the dropdown menu. It will send over
     * the color to the lobby method colorChangeHandler.
     *
     */

    @FXML
    public void handlechangeColor(){ //if a player picks a color that color will be sent in the database and updated for the player
        boolean ok = false;
        Color color = (Color) colorpicker.getValue();
        if(color != null){
            System.out.println("hey color is " + color);
            ok = lobby.colorChangeHandler(color);
            if(ok == false){
                error_msg.setText("failed to set to new color");
            }
        }
    }

    /**
     *
     * This method is used when a player enters a code in the GameCodeSubmit controller.
     * We will need to transfer that instance of the lobby object when a player joins the lobby
     * of the host.
     *
     * @param newLobby sends in the instance of the same lobby object
     */

    public static void setLobby(Lobby newLobby){
        lobby = newLobby;
    }

    /**
     *
     * This method handles a player who wants to return to the main menu.
     * It will differentiate between the host and a player.
     *
     * If the player leaves, the database will remove the instance of
     * the player that was present in the lobby. While the host will
     * delete all the lobby and make every player present in the lobby
     * return to main menu.
     *
     *
     */

    @FXML
    public void handleMainMenu(){
        threadShouldRun = false;
        if(lobby.isHost()){ // it will differentiate the host and a normal player
            MainApp.setDefaultCloseBehavior(stage);
            lobby.hostLeaves();
            stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
        }else{
            lobby.playerLeaves();
            stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
        }
    }

    /**
     *
     * This method is used if a player closes out the window while still in the lobby,
     * this will make sure everything related to the lobby will be deleted.
     *
     */

    public void setCloseBehavior() {
        stage.setOnCloseRequest(event -> {
            if (MainApp.shouldClose()) {
                handleMainMenu();
                MainApp.exit();
            } else {
                event.consume();
            }
        });
    }


    @Override
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * The initialize method will differentiate between a player and the Host.
     *
     * If player is host it will update the lobby observable list and listView.
     *
     * If the player has joined the game of a host it, we will call the userJoined
     * method from lobby and update the observable list.
     *
     */

    @FXML
    private void initialize(){
        if(lobby.isHost()){
            System.out.println("hostid " + lobby.getHostId());
            nicknameplaceholder.setText(UserHandler.getUser().getNickname());
            nicknameplaceholder.setTextFill(Color.RED.getPaint());
            String gamecode = lobby.getGamecode() + "";
            gamecodeplaceholder.setText(gamecode);
            lobby.UpdateLobby();
            players = lobby.getPlayers();
            list.add(players.get(0).getNickname());
            playerlistview.setItems(list);
            progression.setProgress(0.25);
            System.out.println("Initialized lobby");
            colorpicker.setItems(listcoloravailable);

        }else{

            startgame.setVisible(false);
            boolean ok = false;
            lobby = new Lobby();
            nicknameplaceholder.setText(UserHandler.getUser().getNickname());
            String code = gamecode + "";
            System.out.println("game code " + code);
            gamecodeplaceholder.setText(code);
            ok = lobby.userJoined(gamecode);
            System.out.println(ok);
            if(ok == false){
                System.out.println("error with user joined");
            }
            lobby.UpdateLobby();
            players = lobby.getPlayers();
            for(int i = 0; i < players.size(); i++){
                list.add(players.get(i).getNickname());
                if(UserHandler.getUser().getUserId() == players.get(i).getUserID()){
                    nicknameplaceholder.setTextFill(players.get(i).getColor().getPaint());
                }
            }
            playerlistview.setItems(list);
            progression.setProgress(players.size() * 0.25);
            System.out.println("joined lobby");
            colorpicker.setItems(listcoloravailable);
        }

        /**
         *
         * The thread that run the handleRefresh() method
         * every 3 seconds.
         *
         */

        new Thread() {
            @Override
            public void run() {
                while (threadShouldRun) { // thread to call refresh lobby

                    handleRefresh(); // moved closer to boolean check

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }
}
