package univgraphics.primitives;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public abstract class Generator {
    protected int x0, y0;   // left bottom corner coordinates
    protected int width, height;
    protected List<Node> graph = new ArrayList<>();

    public abstract void generate();

    protected boolean intersectOther(Node fromNode, Node toNode) {
        for (Node n : graph) {
            for (Node adj : n.adj()) {
                if (Edge.intersect(fromNode, toNode, n, adj)) {
                    if (!(fromNode.equals(n) || fromNode.equals(adj)
                            || toNode.equals(n) || toNode.equals(adj)))
                        return true;
                }
            }
        }
        return false;
    }

    public List<Node> getGraph() {
        return graph;
    }
}
