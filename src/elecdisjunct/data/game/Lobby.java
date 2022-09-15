package elecdisjunct.data.game;


import elecdisjunct.data.user.UserHandler;
import elecdisjunct.repo.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import elecdisjunct.data.util.Color;

/**
 * Main class that controls all with the lobby.
 *
 * @author Patrick Helvik Legendre
 */


public class Lobby extends Thread{

    private int hostId = -1;
    private ArrayList<Player> player = new ArrayList<Player>();
    private int gamecode = -1;
    private int matchId = -1;

    /**
     *
     * Lobby first constructor will be only initiated
     * by the user which selects the "create match" option
     *
     * The first thing it will create is a gamecode for a specific
     * match which the user has created. That gamecode is used by
     * other players to join the game.
     *
     * Then it will call the method createLobby which will perform
     * various SQL updates to the database.
     *
     * At the end it will create a player object for the user
     * who created the match and we will add that player object
     * in an ArrayList.
     *
     * @param userId userId the user's userId.
     */
    public Lobby(int userId) {
        this.hostId = userId;
        CreateGameCode();
        String color = null;
        try (LobbyDAO lobbyDAO = new LobbyDAO()){

            lobbyDAO.createLobby(hostId, gamecode);

            if(matchId == -1){
                findMatchid();
            }
            System.out.println(getMatchId());
        }catch (SQLException sq) {
            sq.printStackTrace();
        }

        try ( UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){
            color = updateLobbyDAO.getPlayerColor(hostId, matchId);

        }catch (SQLException sq){
            sq.printStackTrace();
        }
        if(color == null){
            System.out.println("hello " + false);
            throw new IllegalArgumentException("color can't be null");
        }
        System.out.println(hostId);
        System.out.println(UserHandler.getUser().getNickname());
    }


    /**
     * Other constructor used for the user who joins the lobby of
     * an other user.
     *
     */

    public Lobby(){ }

    /**
     *
     * private method that is only used to create a game code for the lobby.
     * It is only used by the user who creates lobby.
     *
     * It will generate a random 6 digits code and
     * checks with the database if the code is used
     * by another lobby by calling verifyGameCode method.
     *
     */

    // method that generates a code for the lobby
    private void CreateGameCode(){
        int randomInt = 0;
        boolean ok = false;
        try (UpdateLobbyDAO u = new UpdateLobbyDAO()) {

            while (!ok){
                Random random = new Random();
                randomInt = random.nextInt(899999) + 100000;
                if(!u.verifyGameCode(randomInt)) {
                    ok = true;
                }
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        gamecode = randomInt;
    }


    public int getGamecode(){
        return gamecode;
    }

    public int getHostId(){
        return hostId;
    }

    /**
     * The method checks if the hostId registered when initiating the constructor
     * is the same as the user register on that computer.
     *
     * This method is made to differentiate if a player is the host or is not the
     * host.
     *
     * @return a boolean as a confirmation
     */

    public boolean isHost(){
        if(hostId == UserHandler.getUser().getUserId() || hostId != -1){
            return true;
        }
        return false;
    }

    /**
     * Method to get the ArrayList of players used to
     * update the lobby when a new user joins the lobby.
     *
     * @return ArrayList of players.
     */

    public ArrayList<Player> getPlayers() {
        return player;
    }


    public int getMatchId() {
        return matchId;
    }

    /**
     * method that calls the database to retrieve the
     * matchId for that match, using the gamecode for that lobby.
     *
     * Then it will update the variable matchId.
     *
     */


    public void findMatchid(){
        if(gamecode == 0 || gamecode < 0){
            throw new IllegalArgumentException("gamecode can't be -1");
        }
        try(UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()) {
            matchId = updateLobbyDAO.getMatchid(gamecode);
            if(matchId == -1){
                System.out.println("illegal match_id");
                throw new IllegalArgumentException("match id can't be -1");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }

    /**
     *
     * If a user(host) goes back to main menu after creating the lobby
     * we will need to update the database. This means we will have to
     * delete all information related to the lobby.
     *
     */

    public void hostLeaves(){
        if(matchId == -1){
            findMatchid();
        }
        try (LobbyDAO lobbyDAO = new LobbyDAO()) {
                lobbyDAO.deleteLobby(gamecode, hostId, matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
        }

    }

    /**
     * if a player returns to the main menu, we will have to update
     * the database and for other users.
     *
     * this method is the one to call to update the database when a player
     * leaves.
     *
     */


    public void playerLeaves(){
        if(matchId == -1){
            findMatchid();
        }
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){
            updateLobbyDAO.userLeft(gamecode, UserHandler.getUser().getUserId(), matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }

    /**
     * This method is used to verify the game code.
     *
     * It is going to be used for users that wishes
     * to join a lobby that another user has created
     * @param usergamecode a code submitted from the user through the submit controller.
     * @return boolean as a status
     *
     */

    public boolean VerifyGameCode(int usergamecode){
        boolean ok = false;
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()) {
                ok = updateLobbyDAO.verifyGameCode(usergamecode);
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return ok;
    }

    /**
     * Start game will update the database by deleting
     * lobby in the database and updating match so it can start.
     *
     */
    public void StartGame(){
        if(matchId == -1){
            getMatchId();
        }
        try (LobbyDAO lobbyDAO = new LobbyDAO()){
            lobbyDAO.startGame(matchId, gamecode);
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }



    /**
     * method only used for the user that joins a game that already exists
     * by using the gamecode the user entered.
     *
     * The gamecode is going to get verified and if the code
     * is correct the user will be added to the database
     * and the player ArrayList.
     *
     * The player will be assigned unique color but can be changed
     * in the lobby menu.
     *
     * @param usergamecode a code submitted from the user through the submit controller.
     * @return boolean as a status
     */

    public boolean userJoined(int usergamecode){
        String color;
        boolean ok = false;
        if(usergamecode == 0 || usergamecode < 0){
            return false;
        }

        try (LobbyDAO lobbyDAO = new LobbyDAO();
             UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()) {

            ok = updateLobbyDAO.verifyGameCode(usergamecode);
            if(ok == false){
                return false;
            }
            gamecode = usergamecode;
            if(matchId == -1){
                findMatchid();
            }
            System.out.println(matchId);
            lobbyDAO.joinLobby(gamecode, UserHandler.getUser().getUserId(), matchId);
            color = updateLobbyDAO.getPlayerColor(UserHandler.getUser().getUserId(), matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }
        if(color == null){
            System.out.println("color can't be null");
            throw new IllegalArgumentException("color can't be null");
        }
        System.out.println(UserHandler.getUser().getUserId());
        System.out.println(UserHandler.getUser().getNickname());
        System.out.println(Color.valueOf(color.toUpperCase()));
        Player player2 = new Player(UserHandler.getUser().getUserId(), UserHandler.getUser().getNickname(), Color.valueOf(color.toUpperCase()));
        player.add(player2);
        return true;
    }




    /**
     * This method is run by the gui to update
     * all users present in the lobby.
     *
     * fetchPlayersInLobby retrieves all players present
     * in the lobby.
     *
     * updates the player ArrayList variable,
     * from the database.
     *
     * If you want to retrieve the player ArrayList
     * you can get via getPlayers() method.
     *
     */

    public void UpdateLobby(){
        player.clear();
        if(matchId == -1){
            findMatchid();
        }
        try (PlayerDAO playerDAO = new PlayerDAO()){
            player = playerDAO.fetchPlayersInLobby(matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }

    /**
     * Method that retrieves the colors that have not been claimed
     * from database and the color class.
     *
     * It will first check the database which colors have been
     * claimed in the database and then call the color method that returns
     * the colors to the controller that have not been claimed yet.
     *
     * @return ArrayList of colors that have not been claimed.
     */

    public ArrayList<Color> colorRemaining(){
        ArrayList<String> colors = new ArrayList<>();
        ArrayList<Color> newcolors = new ArrayList<>();
        if(matchId == -1){
            getMatchId();
        }
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){
            colors = updateLobbyDAO.getColors(matchId);
        }catch (SQLException sq) {
            sq.printStackTrace();
        }
        for(int i = 0; i < colors.size(); i++){
            newcolors.add(Color.valueOf(colors.get(i).toUpperCase()));
        }
        return Color.unclaimedColors(newcolors);
    }

    /**
     * method called by the user who joined a lobby.
     * The host will start the game. To synchronize
     * the process for other players, the other users
     * need to check with the database if the game has started.
     *
     *
     * @return a boolean which represents the status if the game has
     * started or not.
     */

    public boolean checkGameStatus(){
        boolean ok = false;
        if(matchId == -1){
            return false;
        }
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){
            ok = updateLobbyDAO.checkIfGameStarted(matchId);
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return ok;
    }

    /**
     *
     * This method is called by the controller when a player
     * have decided to change his color from the lobby.
     *
     * This color will be sent to the database to be
     * updated.
     *
     * The player object will receive the new color chosen
     * by the player, by pulling the database.
     *
     * @param color object for the Color Class
     * @return a boolean as a status
     */

    public boolean colorChangeHandler(Color color){
        boolean ok = false;
        if(matchId == 0 || matchId < 0 || color == null){
            return false;
        }
        String newcolor = color + "";
        try (UpdateLobbyDAO updateLobbyDAO = new UpdateLobbyDAO()){

            ok = updateLobbyDAO.changeColor(matchId, UserHandler.getUser().getUserId(), newcolor);
        }catch (SQLException sq){
            sq.printStackTrace();
            return false;
        }
        System.out.println(ok);
        return ok;
    }



}
