package elecdisjunct.data.game;

/**
 * The Map class keeps track of the lines and nodes on the map
 * and thus takes care of the logic regarding claiming.
 *
 * @author Tore Bergebakken
 * @author Mia Fornes
 */

class Map {

    private Node[] nodes;
    private Line[] lines;
    //private Graph graph; TODO implement something that utilizes graph theory

    /**
     * Where the map is created<br/>
     * - just one default map for now.
     */
    Map() { //

        nodes = new Node[36];
        nodes[0] = new WindmillPark(130, 81, 1); //First player
        nodes[1] = new WindmillPark(1013, 81, 1); //Second player
        nodes[2] = new WindmillPark(1013, 823, 1); //Third player // second if only two
        nodes[3] = new WindmillPark(130, 828, 1); //Fourth player
        nodes[4] = new WindmillPark(299, 105, 1);
        nodes[5] = new CoalPowerPlant(466, 143, 1);
        nodes[6] = new GasPowerPlant(641, 95, 1);
        nodes[7] = new SolarPark(884, 119, 1);
        nodes[8] = new GasPowerPlant(985, 213, 1);
        nodes[9] = new CoalPowerPlant(815, 228, 1);
        nodes[10] = new HydroelectricPowerPlant(630, 261, 1);
        nodes[11] = new City(502, 243);
        nodes[12] = new GasPowerPlant(356, 228, 1);
        nodes[13] = new GasPowerPlant(149, 204, 1);
        nodes[14] = new SolarPark(239, 333, 1);
        nodes[15] = new CoalPowerPlant(356, 410, 1);
        nodes[16] = new FissionReactor(499, 389, 4);
        nodes[17] = new FusionReactor(661, 389, 4);
        nodes[18] = new City(798, 380);
        nodes[19] = new HydroelectricPowerPlant(968, 369, 1);
        nodes[20] = new CoalPowerPlant(1013, 493, 1);
        nodes[21] = new WindmillPark(814, 529, 1);
        nodes[22] = new FissionReactor(661, 528, 4);
        nodes[23] = new FusionReactor(499, 528, 4);
        nodes[24] = new City(287, 562);
        nodes[25] = new HydroelectricPowerPlant(116, 484, 1);
        nodes[26] = new WindmillPark(174, 675, 1);
        nodes[27] = new SolarPark(353, 723, 1);
        nodes[28] = new CoalPowerPlant(499, 675, 1);
        nodes[29] = new City(665, 691);
        nodes[30] = new CoalPowerPlant(772, 620, 1);
        nodes[31] = new SolarPark(911, 598, 1);
        nodes[32] = new GasPowerPlant(1008, 676, 1);
        nodes[33] = new WindmillPark(807, 744, 1);
        nodes[34] = new GasPowerPlant(503, 792, 1);
        nodes[35] = new HydroelectricPowerPlant(263, 831, 1);


        lines = new Line[69];
        lines[0] = new Line(nodes[0], nodes[4]);
        lines[1] = new Line(nodes[4], nodes[5]);
        lines[2] = new Line(nodes[5], nodes[6]);
        lines[3] = new Line(nodes[6], nodes[7]);
        lines[4] = new Line(nodes[7], nodes[1]);
        lines[5] = new Line(nodes[1], nodes[8]);
        lines[6] = new Line(nodes[8], nodes[7]);
        lines[7] = new Line(nodes[8], nodes[9]);
        lines[8] = new Line(nodes[7], nodes[9]);
        lines[9] = new Line(nodes[6], nodes[9]);
        lines[10] = new Line(nodes[9], nodes[10]);
        lines[11] = new Line(nodes[6], nodes[10]);
        lines[12] = new Line(nodes[10], nodes[11]);
        lines[13] = new Line(nodes[5], nodes[11]);
        lines[14] = new Line(nodes[11], nodes[12]);
        lines[15] = new Line(nodes[4], nodes[12]);
        lines[16] = new Line(nodes[4], nodes[13]);
        lines[17] = new Line(nodes[0], nodes[13]);
        lines[18] = new Line(nodes[13], nodes[14]);
        lines[19] = new Line(nodes[14], nodes[12]);
        lines[20] = new Line(nodes[12], nodes[15]);
        lines[21] = new Line(nodes[14], nodes[15]);
        lines[22] = new Line(nodes[15], nodes[16]);
        lines[23] = new Line(nodes[16], nodes[11]);
        lines[24] = new Line(nodes[16], nodes[17]);
        lines[25] = new Line(nodes[17], nodes[10]);
        lines[26] = new Line(nodes[17], nodes[18]);
        lines[27] = new Line(nodes[18], nodes[9]);
        lines[28] = new Line(nodes[18], nodes[19]);
        lines[29] = new Line(nodes[8], nodes[19]);
        lines[30] = new Line(nodes[19], nodes[20]);
        lines[31] = new Line(nodes[19], nodes[21]);
        lines[32] = new Line(nodes[20], nodes[21]);
        lines[33] = new Line(nodes[21], nodes[18]);
        lines[34] = new Line(nodes[21], nodes[22]);
        lines[35] = new Line(nodes[22], nodes[17]);
        lines[36] = new Line(nodes[22], nodes[23]);
        lines[37] = new Line(nodes[23], nodes[16]);
        lines[38] = new Line(nodes[23], nodes[24]);
        lines[39] = new Line(nodes[24], nodes[15]);
        lines[40] = new Line(nodes[15], nodes[25]);
        lines[41] = new Line(nodes[14], nodes[25]);
        lines[42] = new Line(nodes[25], nodes[24]);
        lines[43] = new Line(nodes[25], nodes[26]);
        lines[44] = new Line(nodes[26], nodes[24]);
        lines[45] = new Line(nodes[24], nodes[27]);
        lines[46] = new Line(nodes[27], nodes[28]);
        lines[47] = new Line(nodes[28], nodes[23]);
        lines[48] = new Line(nodes[28], nodes[29]);
        lines[49] = new Line(nodes[29], nodes[22]);
        lines[50] = new Line(nodes[29], nodes[30]);
        lines[51] = new Line(nodes[30], nodes[21]);
        lines[52] = new Line(nodes[30], nodes[31]);
        lines[53] = new Line(nodes[31], nodes[20]);
        lines[54] = new Line(nodes[20], nodes[32]);
        lines[55] = new Line(nodes[31], nodes[32]);
        lines[56] = new Line(nodes[32], nodes[33]);
        lines[57] = new Line(nodes[33], nodes[30]);
        lines[58] = new Line(nodes[33], nodes[2]);
        lines[59] = new Line(nodes[32], nodes[2]);
        lines[60] = new Line(nodes[33], nodes[34]);
        lines[61] = new Line(nodes[34], nodes[29]);
        lines[62] = new Line(nodes[34], nodes[27]);
        lines[63] = new Line(nodes[34], nodes[35]);
        lines[64] = new Line(nodes[35], nodes[27]);
        lines[65] = new Line(nodes[27], nodes[26]);
        lines[66] = new Line(nodes[26], nodes[35]);
        lines[67] = new Line(nodes[26], nodes[3]);
        lines[68] = new Line(nodes[35], nodes[3]);


        //nodes[3].setOwner(new Player(1337, "dolan", Color.RED)); // ye olde temporary thing

        //this.graph = new Graph();
        // fill this thing up when you use it...

    }

    Node[] getNodes() {
        return nodes;
    }

    Line[] getLines() {
        return lines;
    }

    /**
     * Claiming a <i>node</i> requires the player to own a connected line
     *
     * @param node      node to claim
     * @param player    claimant
     * @return
     */
    boolean canClaim(Node node, Player player) {

        if (node.isClaimed() || player == null) return false;

        // loop thru, find a line that is connected to the node
        // check if that line's owner is the player - if so, the player can claim.
        for (Line line : lines) {
            if (line.getTo().equals(node) || line.getFrom().equals(node)) {
                if (player.equals(line.getOwner())) { // thus avoiding that null check
                    return true;
                }
            }
        }

        return false; // found no line matching the criteria
    }

    /**
     * Claiming a <i>line</i> requires the player to own one of the endpoints (for now)
     *
     * @param line      line to claim
     * @param player    claimant
     * @return
     */
    boolean canClaim(Line line, Player player) {

        return player != null &&
                (player.equals(line.getFrom().getOwner()) ||
                        player.equals(line.getTo().getOwner()));
    }

    /**
     * reroutes canClaim calls
     */
    boolean canClaim(MapComponent mapComponent, Player player) {
        if (mapComponent instanceof PowerPlant) {
            return canClaim((PowerPlant) mapComponent, player);
        } else if (mapComponent instanceof Line) {
            return canClaim((Line) mapComponent, player);
        } else if (mapComponent instanceof City) {
            //System.err.println("No cities may be claimed atm, why did this happen?"); it's okay
            return false;
        } else {
            System.err.println("A claim was attempted with some alien implementation of MapComponent");
            return false;
        }
    }

    /**
     * Method for claiming a node/line, preeetty straightforward.<br/>
     * Checks if the player should be able to claim it first.
     *
     * @param mapComponent  component to claim
     * @param player        claimant
     * @return
     */
    boolean claim(MapComponent mapComponent, Player player) {
        if (canClaim(mapComponent, player)) {
            mapComponent.setOwner(player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This is how simply we can do the total calculation for now.<br/>
     * Just a sum of all the throughputs from a player's line.
     *
     * @param player    The player to calculate total per-round score for
     * @return          Score accumulated this turn (can be used as a temporary thing)
     */
    int getTotalOutput(Player player) {

        int total = 0;

        for (Line line : lines) {

            if (player.equals(line.getOwner())) {

                total += line.getThroughput();
            }
        }

        return total;

    }

    /**
     * Method to get total lines owned by a player

     */
    int getTotalLines(Player player){
        int total = 0;

        for(Line line : lines){
            if(player.equals(line.getOwner())){
                total ++;
            }
        }
        return total;
    }

    /**
     * Method to get total nodes owned by a player
     */
    int getTotalNodes(Player player){
        int total = 0;

        for(Node node : nodes){
            if(player.equals(node.getOwner())){
                total++;
            }
        }
        return total;
    }

    /**
     * Updates a node in our list according to info from dummy object.
     */
    void update(Node updatedNode) {

        for (Node node : nodes) {

            if (node.equals(updatedNode) && node instanceof PowerPlant) {

                //PowerPlant powerPlant = (PowerPlant) node;
                //PowerPlant updatedPlant = (PowerPlant) updatedNode;

                node.setOwner(updatedNode.getOwner());
                node.setLevel(updatedNode.getLevel());
                node.setBroken(updatedNode.isBroken());

                break; // get out when done. not more than one instance of the same node in the map's array.

            }
        }
    } // END updateNode()

    /**
     * Updates a line in our list according to info from dummy object.
     */
    void update(Line updatedLine) {

        for (Line line : lines) {

            if (line.equals(updatedLine)) {

                line.setOwner(updatedLine.getOwner());
                line.setLevel(updatedLine.getLevel()); // lines don't have levels right now but they might have later
                line.setBroken(updatedLine.isBroken());
                break;

            }

        }

    }

}
