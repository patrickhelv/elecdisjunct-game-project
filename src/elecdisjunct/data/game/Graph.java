package elecdisjunct.data.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Not actually used, but it could have been possible to utilize graph theory for calculating score in some more sophisticated way,
 * and perhaps for other uses (like having sabotage cascade to connected lines).
 *
 * Based on code by
 * <a href="https://gist.github.com/imamhidayat92/dff60e5554020bd58b64">imamhidayat92</a>
 *
 * @author imamhidayat92, Mia Fornes
 */

public class Graph {

    final private HashMap<Node, Set<Node>> list;

    public Graph() {
        this.list = new HashMap<>();
    }

    public boolean addNode(Node node) {
        if(list.containsKey(node)) {
            return false; //Node already exists
        }
        list.put(node, new HashSet<Node>());
        return true;
    }

    public void addLine(Node fromNode, Node toNode) {
        list.get(fromNode).add(toNode);
        //Add toNode to fromNode to make it a undirected graph
        list.get(toNode).add(fromNode);
    }

    public boolean isUndirectedConnected(Node fromNode, Node toNode) {
        if (list.get(fromNode).contains(toNode) && list.get(toNode).contains(fromNode)) {
            return true;
        }
        return false;
    }

    public Iterable<Node> getNeighbors(Node node) {
        return list.get(node);
    }

    public Iterable<Node> getAllNodes() {
        return list.keySet();
    }
}
