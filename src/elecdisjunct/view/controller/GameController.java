package elecdisjunct.view.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import elecdisjunct.data.game.*;
import elecdisjunct.data.user.User;
import elecdisjunct.data.user.UserHandler;
import elecdisjunct.data.util.Color;
import elecdisjunct.view.launcher.MainApp;
import elecdisjunct.view.stage.SceneName;
import elecdisjunct.view.stage.Stageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * Controller for the game itself.
 *
 * @author Tore Bergebakken
 * @author Victoria Blichfeldt
 * @author Mia Fornes
 */
public class GameController implements Stageable {
    private Stage stage;

    private ObservableList<Player> playersObservableList;

    private static final int NODE_RADIUS_BASE = 16, NODE_RADIUS_FACTOR = 4, NODE_STROKE_WIDTH = 5, LINE_WIDTH_FACTOR = 7;
    //private static final int WIDTH_SCALE_FACTOR = 200, HEIGHT_SCALE_FACTOR = 200;
    //private static final int WIDTH_POSITION_FACTOR = 1, HEIGHT_POSITION_FACTOR = 1; we ain't gonna want this now

    private String NL = System.lineSeparator();
    //private Player thisPlayer = new Player(1337, "dolan", Color.RED); // this was a temporary bogus player. still exists in MapTest.

    private static MatchAbstraction match;

    private MapComponent selected = null; // holds the selected node or line

    private boolean roundWarningShown = false;
    private boolean sabotageMessageShown = false;
    private boolean endMessageShown = false;

    @FXML
    private Label roundHeading, scoreBlock, infoHeading, infoBlock, helpBlock;

    @FXML
    private JFXButton claimButton, upgradeButton, sabotageButton, endTurnButton, resultButton, leaveButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private Pane mapPane;

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private TableColumn<ObservableList<Player>, String> playerColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn, nodeColumn, lineColumn;

    // also very temporary
    @FXML
    private CheckBox becomeToddCheckBox;

    private User todd = new User(15, "todd@testers.com", "todd");
    private User dolan = new User(15, "dolan@testers.com", "dolan");

    private void becomeTodd() {
        if (dolan.equals(UserHandler.getUser())) {
            UserHandler.setUser(todd);
        } else {
            UserHandler.setUser(dolan);
        }
        ((MatchSimulator) match).changeYourself();

    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void setMatch(MatchAbstraction newMatch) {
        match = newMatch;
    }

    @FXML
    public void initialize() {
        hideButtons();
        sabotageButton.setVisible(false);
        generateMapComponents();
        refreshGUI();

        becomeToddCheckBox.setVisible(false);
        // placeholder
        if (match.isSimulated()) {
            endTurnButton.setOnAction(e -> match.endTurn());
            becomeToddCheckBox.setDisable(false);
            becomeToddCheckBox.setVisible(true);
            becomeToddCheckBox.setOnAction(e -> becomeTodd());
            leaveButton.setVisible(false);
            //leaveButton.setOnAction(e -> match.leave());
        } else {
            endTurnButton.setVisible(false);
            leaveButton.setOnAction(e -> leave());
        }

        mapPane.setOnContextMenuRequested(e -> deselect());

        match.getMatch().turnCountProperty().addListener((observable, oldValue, newValue) -> refreshGUI());

        generatePlayerTable();
    }


    private void generatePlayerTable() {
        match.updateComponentCounts();
        //playerTable.setItems(null);
        playersObservableList = FXCollections.observableArrayList( match.getMatch().getPlayers());

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        nodeColumn.setCellValueFactory(new PropertyValueFactory<>("nodes"));
        lineColumn.setCellValueFactory(new PropertyValueFactory<>("lines"));

        playerColumn.setCellFactory(e -> new TableCell<ObservableList<Player>, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty) {
                    for (Player p : match.getMatch().getPlayers()) {
                        if (item.equals(p.getNickname())) {
                            this.setText(p.getNickname() + (p.isPresent()? "" : " (has left)")); // should work
                            this.setTextFill(p.getColor().getPaint());
                            this.setStyle("-fx-font-weight: bold");
                        }
                    }
                }
            }
        });
        playerTable.setItems(playersObservableList);
    }

    private void refreshPlayerTable(){
        playerTable.getItems().clear();
        playersObservableList = FXCollections.observableArrayList(match.getMatch().getRankedPlayers()); // only change needed 4 rank in game
        playerTable.setItems(playersObservableList);
    }



    /**
     * This method uses the map's lists of nodes and lines to generate the content of the JavaFX pane<br/>
     * - complete with event handling and property binding. Calls separate methods for setting them up.
     *
     */
    private void generateMapComponents() {

        Node[] nodes = match.getNodes();
        Line[] lines = match.getLines();

        Circle[] fxNodes = new Circle[nodes.length];
        javafx.scene.shape.Line[] fxLines = new javafx.scene.shape.Line[lines.length];

        for (int i = 0; i < lines.length; i++) {
            fxLines[i] = setUpComponent(lines[i]);
        }

        for (int i = 0; i < nodes.length; i++) {
            fxNodes[i] = setUpComponent(nodes[i]);
        }

        mapPane.getChildren().add(new Circle(23, 23, 0.0, javafx.scene.paint.Color.TRANSPARENT)); // in place of selection indicator
        mapPane.getChildren().addAll(fxLines);
        mapPane.getChildren().addAll(fxNodes);

    }

    /**
     * This method loops through the lists of actual map components,<br/>
     * adding a selection indicator where it should be<br/>
     * and marking sabotaged power plants.
     */
    private void refreshMapComponents() {

        Node[] nodes = match.getNodes();
        Line[] lines = match.getLines();
        javafx.scene.Node fxSelected = null;
        ArrayList<javafx.scene.Node> fxSabotageIndicators = new ArrayList<>();

        for (Line line : lines){
            if (line.equals(selected)) {
                fxSelected = new javafx.scene.shape.Line(line.getFrom().getPosX(), line.getFrom().getPosY(),
                                                         line.getTo().getPosX(), line.getTo().getPosY());
                ((javafx.scene.shape.Line) fxSelected).setStroke(Color.SELECTED.getPaint());
                ((javafx.scene.shape.Line) fxSelected).setStrokeWidth(LINE_WIDTH_FACTOR * 2);
            }
            if (line.isBroken()) {
                System.err.println("BRÖÖKEN LINE");
            }
        }

        for (Node node : nodes) {
            if (node.equals(selected)) {
                fxSelected = new Circle(node.getPosX(), node.getPosY(), NODE_RADIUS_BASE * 1.5 + node.getLevel() * NODE_RADIUS_FACTOR, Color.SELECTED.getPaint());
            }
            if (node.isBroken()) {
                double scale = NODE_RADIUS_FACTOR * node.getLevel() * 0.6 + NODE_RADIUS_BASE;
                javafx.scene.shape.Line line1 = new javafx.scene.shape.Line(node.getPosX() + scale, node.getPosY() + scale, node.getPosX() - scale, node.getPosY() - scale);
                javafx.scene.shape.Line line2 = new javafx.scene.shape.Line(node.getPosX() - scale, node.getPosY() + scale, node.getPosX() + scale, node.getPosY() - scale);
                line1.setStrokeWidth(LINE_WIDTH_FACTOR);
                line1.setStroke(Color.NEUTRAL.getPaint());
                line2.setStrokeWidth(LINE_WIDTH_FACTOR);
                line2.setStroke(Color.NEUTRAL.getPaint());
                fxSabotageIndicators.add(line1);
                fxSabotageIndicators.add(line2);
            }
        }

        if (mapPane.getChildren().size() > 0) {
            if (mapPane.getChildren().size() > 1 + nodes.length + lines.length) {
                //System.out.println("Removing from array with size " + mapPane.getChildren().size() + " from " + (nodes.length + lines.length + 1) + " to " + (mapPane.getChildren().size() - 1));
                mapPane.getChildren().remove(nodes.length + lines.length + 1, mapPane.getChildren().size() - 1);
            }
            mapPane.getChildren().remove(0);

            mapPane.getChildren().add(0, selected != null? fxSelected : new Circle(23, 23, 0.0, javafx.scene.paint.Color.TRANSPARENT)); // will it be in bg? I don't know...
            mapPane.getChildren().addAll(fxSabotageIndicators);
        }

        // set up selection indicator

    }

    // SETTING UP NODES/LINES

    /**
     * Sets up a line as a graphical one to display on the GUI map<br/>
     * Changes color based on which player claims it.
     *
     * @param line  line from map
     * @return      generated JavaFX line to be displayed
     */
    private javafx.scene.shape.Line setUpComponent(Line line) {

        javafx.scene.shape.Line fxLine = new javafx.scene.shape.Line(line.getFrom().getPosX(), line.getFrom().getPosY(),
                line.getTo().getPosX(), line.getTo().getPosY());

        fxLine.setFill(Color.NEUTRAL.getPaint()); // fill vs stroke? what's the difference
        fxLine.setStrokeWidth(LINE_WIDTH_FACTOR); // leveling may change it or what?

        // assign event handler method
        fxLine.setOnMouseClicked(e -> handleClick(e, line));

        // set color
        if (line.getOwner() != null) {
            System.out.println("wtf");
            fxLine.setFill(line.getOwner().getColor().getPaint());
        }
        // add listener
        line.ownerProperty().addListener(
                (observableValue, oldPlayer, newPlayer) -> {if (newPlayer != null) fxLine.setStroke(newPlayer.getColor().getPaint());});

        return fxLine;
    }

    /**
     * Sets up a node as a graphical one to display on the GUI map<br/>
     * Changes color based on which player claims it.<br/>
     * Changes size based on its level<br/>
     * Has an icon based on what type of power plant it is.
     *
     * @param node  node from map
     * @return      generated JavaFX circle to be displayed
     */
    private Circle setUpComponent(Node node) {

        Circle circle;

        circle = new Circle(node.getPosX(), node.getPosY(), NODE_RADIUS_BASE + node.getLevel() * NODE_RADIUS_FACTOR);

        // binding propertied, adding listeners
        node.levelProperty().addListener(
                (observable, oldValue, newValue) -> circle.setRadius(NODE_RADIUS_BASE + newValue.intValue() * NODE_RADIUS_FACTOR));
        node.ownerProperty().addListener(
                (observableValue, oldPlayer, newPlayer) -> {if (newPlayer != null) circle.setStroke(newPlayer.getColor().getPaint());});
        // set color

        if (node.getOwner() != null) {
            circle.setStroke(node.getOwner().getColor().getPaint());
        } else {
            circle.setStroke(Color.NEUTRAL.getPaint());
        }

        circle.setStrokeWidth(NODE_STROKE_WIDTH);
        if (node.getIcon() != null) {
            circle.setFill(new ImagePattern(new Image(
                    new File("resources/".concat(node.getIcon())).toURI().toString()
            )));
        } else {
            circle.setFill(Color.NODE_BG.getPaint());
        }

        circle.setOnMouseClicked(e -> handleClick(e, node));

        return circle;
    }

    private void leave() {
        if (match.leave()) {
            match = null;
            MainApp.setDefaultCloseBehavior(stage);
            stage.setScene(MainApp.getScenes().get(SceneName.GAME_MAIN_MENU).getScene());
        } // no feedback atm
    }

    private void end() {
        ResultController.setPlayers(match.getMatch().getRankedPlayers());
        match = null;
        stage.setScene(MainApp.getScenes().get(SceneName.RESULT).getScene());
    }

    /**
     * Called in FXMLInfo, makes the close button also call the leave method
     */
    public void setCloseBehavior() {
        stage.setOnCloseRequest(event -> {
            if (MainApp.shouldClose()) {
                match.leave(); // no need to set it to null
                MainApp.exit();
            } else {
                event.consume();
            }
        });
    }

    // INFO SECTION

    /**
     * Calls other methods that redraw parts of the GUI,<br/>
     * handling part of what happens when the game ends here.<br/>
     * <br/>
     * Previously called showInfo(), but that soon became an imprecise name.
     */
    private void refreshGUI() {

        hideButtons();

        refreshMapComponents();
        match.updateComponentCounts(); // updates node&line count in table
        refreshPlayerTable();

        if (match.getMatch().isAtEnd() && !endMessageShown) { // dat boolean comes in real handy here
            System.out.println("GUI knows match has ended");
            resultButton.setVisible(true);
            resultButton.setOnAction(e -> end());
            leaveButton.setVisible(false);
            MainApp.setDefaultCloseBehavior(stage);
        }

        showPopUp();
        showRoundInfo(); // might as well, but isn't necessary
        //playerTable.refresh();
        //generatePlayerTable();

        if (selected == null) {
            showDefaultInfo();
        } else {

            showComponentInfo(); // takes care of everything

            if (!match.getMatch().isAtEnd()) showActionButtons(); // they will only be enabled if it's your turn

            showHelp();

        }
    }

    private void hideButtons() {
        claimButton.setVisible(false);
        upgradeButton.setVisible(false);
        sabotageButton.setVisible(false);
        // this might not be necessary but I throw it in for good measure
        claimButton.setDisable(true);
        upgradeButton.setDisable(true);
        sabotageButton.setDisable(true);
    }

    /**
     * Shows information about how the game GUI is used
     */
    private void showDefaultInfo() {

        infoHeading.setText("Select a node or line");
        infoBlock.setText("Click a line or circle to view info about it" + NL +
                "Double-click to claim or upgrade" + NL +
                "(or use the button that appears below)" + NL +
                "Right-click anywhere on the map to show this info again");
        helpBlock.setText("Contextual help appears here");

    }

    private void showComponentInfo() {
        infoBlock.setText(selected.toString());
    }

    /**
     * Shows relatively helpful text based on what kind of map component is selected (and its current state)
     */
    private void showHelp() {

        if (selected == null) {
            System.err.println("why did it go here?");
        } else if (selected instanceof Line) {
            helpBlock.setText("Lines connect power plants and/or cities, transferring electricity." + NL + "The throughput of a line is the total output of its two endpoints.");
        } else if (selected instanceof City) {
            helpBlock.setText("Cities give zero output and cannot be claimed." + NL + "However, it might still be viable to own a line to one.");
        } else if (selected instanceof PowerPlant) {
            if (selected.isBroken()) {
                helpBlock.setText("This power plant has been ruthlessly sabotaged" + NL + "and will yield no output. A true tragedy.");
            } else {
                helpBlock.setText("Power plants produce electricity - lucrative for you, obviously." + NL + "The output of a power plant depends on its type and level.");
            }
        }

    }

    /**
     * Shows info about round count, current score + potential increase, whose turn it is - and some additional contextual info.
     */
    private void showRoundInfo() {
        if (match.getMatch().isAtEnd()) {
            roundHeading.setText("The Game has Ended");
        } else {
            roundHeading.setText("Round " + (match.getMatch().getRoundCount() + 1) + " of " + Match.ROUND_LIMIT);
        }

        StringBuilder bldr = new StringBuilder("Your score:");
        bldr.append(match.getMatch().getThisPlayer().getScore());
        bldr.append(NL);

        bldr.append("Potential increase: ");
        bldr.append(match.getTotalOutput());
        bldr.append(NL);


        if (match.getMatch().isAtEnd()) {
            bldr.append("No action is possible at this point.");
            bldr.append(NL);
            if (match.getMatch().isInFirstPlace()) {
                bldr.append("I'm sure you're happy about that, victorious one.");
            } else {
                bldr.append("I suppose you would've preferred a different outcome, though.");
            }
        } else {

            bldr.append("It is ");
            if (match.getMatch().isAtYourTurn()) {
                bldr.append("your turn.");
            } else {
                bldr.append(match.getMatch().getActivePlayer().getNickname());
                bldr.append("'s turn.");
            }

            if (match.getMatch().isSabotageAllowed()) {
                bldr.append(NL);
                bldr.append("You may perform sabotage.");
            }
        }

        scoreBlock.setText(bldr.toString());
    }

    /**
     * Displays a popup at certain significant points, to notify the player about what's going on
     */
    private void showPopUp() {

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialog.getContent().setBackground(new Background((new BackgroundFill(Color.SELECTED.getPaint(), CornerRadii.EMPTY, Insets.EMPTY)))); // mysterious bee gee

        if (match.getMatch().isCloseToLastRound() && !roundWarningShown) {
            dialogLayout.setBody(new Label("Only 6 rounds left!\n" +
                    (match.getMatch().isInFirstPlace()? "Good luck keeping your position." : "Will you be able to turn the tide?")));
            roundWarningShown = true;

        } else if (match.getMatch().getRoundCount() == Match.SABOTAGE_START && !sabotageMessageShown) {
            dialogLayout.setBody(new Label("You may perform sabotage - once."));
            sabotageMessageShown = true;

        } else if (match.getMatch().isAtEnd() && !endMessageShown) {
            dialogLayout.setBody(new Label("The game has ended.\nYou may inspect the map at your leisure,\nthen press View Results to proceed."));
            endMessageShown = true;

        } else {
            dialog.close();
        }

        dialog.show();

    }

    /**
     * Based on what is selected, the state of that unit and the match itself,<br/>
     * buttons are shown and perhaps enabled. The type header is also updated.
     */
    private void showActionButtons() {

        String typeText = selected instanceof PowerPlant ? "Power Plant" : selected.getClass().getSimpleName();

        if (selected.isClaimed()) {

            if (match.getMatch().getThisPlayer().equals(selected.getOwner())) {
                infoHeading.setText("Your " + typeText);

                if (selected instanceof PowerPlant) {

                    upgradeButton.setVisible(true);

                    if (selected.canUpgrade() && match.getMatch().isAtYourTurn() && !match.getMatch().isAtEnd()) {
                        upgradeButton.setDisable(false);
                        upgradeButton.setOnAction(e -> performAction());
                    } else {
                        upgradeButton.setDisable(true);
                    }

                }

            } else {
                infoHeading.setText(selected.getOwner().getNickname() + "'s " + typeText);

                if (match.canSabotage(selected)) {

                    sabotageButton.setVisible(true); // visible if you can sabotage

                    if (match.getMatch().isAtYourTurn() && !match.getMatch().isAtEnd()) { // enabled if it's your turn
                        sabotageButton.setDisable(false);
                        sabotageButton.setOnAction(e -> performAction());
                    }else{
                        sabotageButton.setDisable(true);
                    }                    // show button at turn 20 when clicked on hide button
                }

            }

        } else {

            infoHeading.setText("Unclaimed " + typeText);

            // show disabled button
            claimButton.setVisible(true);

            if (match.canClaim(selected) && match.getMatch().isAtYourTurn() && !match.getMatch().isAtEnd()) {

                claimButton.setDisable(false); // leads to claiming
                claimButton.setOnAction(e -> performAction());

            } else {

                claimButton.setDisable(true); // make sure it is disabled

            }

        }

    }

    // SELECTION AND CONTROL SECTION

    private void deselect() {
        selected = null;
        refreshGUI();
        // show info
    }

    /**
     * Handles mouse clicks on map components, swapping out the selected object, refreshing the GUI to show info about it,<br/>
     * and going straight at performing the associated action it's a double-or-more-click.<br/>
     * Right-clicking is handled as OnContextMenuRequested for the pane containing the map, no point in putting it here.
     *
     * @param e             MouseEvent that is interpreted here
     * @param mapComponent  the actual game component that is connected to the graphical circle/line that is clicked
     */
    private void handleClick(MouseEvent e, MapComponent mapComponent) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            selected = mapComponent;
            // show info or go straight to action
            refreshGUI();
            if (e.getClickCount() > 1) {
                performAction();
            }

        }

    }

    /**
     * Called when you click an action button *or* double-click on a line/node<br/>
     * Performs the only action possible since these checks won't all be successful,<br/>
     * then refreshes the GUI yet again.
     */
    private void performAction() {

        if (!match.getMatch().isAtYourTurn() || match.getMatch().isAtEnd() || !match.getMatch().getThisPlayer().isPresent()) return; // gtfo

        if (selected.isClaimed() && selected.getOwner().equals(match.getMatch().getThisPlayer())) {
            match.upgrade(selected);
        } else if (match.canClaim(selected)) {
            match.claim(selected);
        } else if (match.canSabotage(selected)) { // actual check to condition added
            match.sabotage(selected);
        }

        refreshGUI();

    }

}
