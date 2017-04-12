package univgraphics.lab2;

import univgraphics.primitives.Edge;
import univgraphics.primitives.Generator;
import univgraphics.primitives.Node;
import univgraphics.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 01.04.2017.
 * All code is free to use and distribute.
 */
public class PlanarGenerator extends Generator{


    public PlanarGenerator(int x0, int y0, int width, int height) {
        super(x0, y0, width, height);
    }

    public void generate() {
        graph.clear();
        int verNum = 3 + (int) (Math.random() * 5);
        for (int i = 0; i < verNum; i++) {
            Point randomPoint = Point.createRandomPoint(x0, y0, width, height);
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
}
