package univgraphics.lab1;

import univgraphics.primitives.Edge;
import univgraphics.primitives.Localizator;
import univgraphics.primitives.Node;
import univgraphics.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class SimpleLocalizator extends Localizator {
    public SimpleLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
    }

    @Override
    public List<Edge> locate() {
        if (graph.size() < 3) return null;

        List<Edge> edges = new ArrayList<>();
        Node prevNode = graph.get(0);
        Node currNode = prevNode.adj().iterator().next();
        Node nextNode = null;
        edges.add(new Edge(prevNode, currNode));
        while (nextNode != graph.get(0)) {
            Node finalPrevNode = prevNode;
            nextNode = currNode.adj()
                    .stream()
                    .filter(x -> x != finalPrevNode)
                    .findFirst()
                    .orElse(null);
            edges.add(new Edge(currNode, nextNode));
            prevNode = currNode;
            currNode = nextNode;
        }

        int leftmostX = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(graph.get(0))
                .getX();
        Point farPoint = new Point(leftmostX - 1, pointToLocate.getY());
        Edge testEdge = new Edge(pointToLocate, farPoint);

        int counter = 0;
        for (int i = 0; i < edges.size();) {
            if (Edge.intersects(testEdge, edges.get(i))) {
                if (graph.contains(new Node(Edge.getIntersectionPoint(testEdge, edges.get(i))))) {
                    int prevY = testEdge.getEnd().getY();
                    testEdge.getEnd().setY(prevY + 1);
                    counter = 0;
                    i = 0;
                    continue;
                } else {
                    counter++;
                }
            }
            i++;
        }
        if (counter % 2 == 0) {
            return null;
        }
        return edges;
    }
}
