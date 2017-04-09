package sample;

import sample.primitives.Edge;
import sample.primitives.Node;
import sample.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 01.04.2017.
 * All code is free to use and distribute.
 */
public class PlanarPartition {

    private int x0, y0;   // left bottom corner coordinates
    private int width, height;
    private List<Node> graph = new ArrayList<>();

    PlanarPartition(int x0, int y0, int width, int height) {
        this.x0 = x0;
        this.y0 = y0;
        this.width = width;
        this.height = height;
    }

    private Point createRandomPoint() {
        int x = x0;
        int y = y0;
        x += Math.random() * width;
        y += Math.random() * height;
        return new Point(x, y);
    }

    void generate() {
        graph.clear();
        int verNum = 3 + (int) (Math.random() * 5);
        for (int i = 0; i < verNum; i++) {
            Point randomPoint = createRandomPoint();
            Node nextNode = new Node(randomPoint.getX(), randomPoint.getY());
            graph.add(nextNode);
        }

        int ribNum = 3 * verNum - 6; //some smart formula
        for (int numOfRibs = 0, counter = 0;
             numOfRibs < ribNum && counter < verNum * 1000;
             counter++) {

            Node fromNode = graph.get((int)(Math.random() * verNum));
            Node toNode = graph.get((int)(Math.random() * verNum));

            if (!fromNode.equals(toNode) && !intersectOther(fromNode, toNode) && !fromNode.adj().contains(toNode)) {
                fromNode.adj().add(toNode);
                toNode.adj().add(fromNode);
                numOfRibs++;
            }
        }
    }

    private boolean intersectOther(Node fromNode, Node toNode) {
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
