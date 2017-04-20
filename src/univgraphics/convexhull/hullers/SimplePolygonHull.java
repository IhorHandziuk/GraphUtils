package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class SimplePolygonHull extends Huller {

    private Node leftmostXNode, rightmostXNode;

    public SimplePolygonHull(List<Node> graph) {
        super(graph);
        leftmostXNode = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(graph.get(0));
        rightmostXNode = graph
                .stream()
                .max(Comparator.comparingInt(Point::getX))
                .orElse(graph.get(0));
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;

        List<Node> upperChain = new ArrayList<>();
        List<Node> lowerChain = new ArrayList<>();
        Edge dividingEdge = new Edge(leftmostXNode, rightmostXNode);
        for (Node node : graph) {
            if (dividingEdge.pointIsOnRightSide(node)) {
                lowerChain.add(node);
            } else {
                upperChain.add(node);
            }
        }
        upperChain.sort(Comparator.comparingInt(Point::getX));
        lowerChain.sort(Comparator.comparingInt(Point::getX));

        Stack<Point> upperPart = getChainHull(upperChain);
        //Stack<Point> lowerPart = getChainHull(lowerChain);

        List<Point> convexHull = new ArrayList<>();
        convexHull.addAll(upperPart);
        //convexHull.addAll(lowerPart);
        return convexHull;
    }

    private Stack<Point> getChainHull(List<Node> chain) {
        Node first = chain.get(0);
        Node prevNode = new Node(first.getX(), first.getY() - 2); // 2 is arbitrary number
        Node currNode = first;
        Node nextNode = chain.get(1);

        Stack<Point> halfHull = new Stack<>();
        halfHull.push(prevNode);
        halfHull.push(currNode);
        //int nextPointIndex
        while (nextNode != rightmostXNode) {
            Edge checkRightTurn = new Edge(prevNode, nextNode);
            if (!checkRightTurn.pointIsOnRightSide(currNode)) {
                Edge checkLastPoint = new Edge(currNode, rightmostXNode);
                if (checkLastPoint.pointIsOnRightSide(nextNode)) {
                    halfHull.push(nextNode);

                } else {
                    // delete curr
                }
                //prevNode = currNode;
            } else {
                // delete stack top
            }

            Node finalPrevNode = prevNode;
            nextNode = currNode.adj()
                    .stream()
                    .filter(x -> x != finalPrevNode)
                    .findFirst()
                    .orElse(null); // never got
            if ((new Edge(prevNode, nextNode)).pointIsOnRightSide(currNode)) {
                prevNode = currNode;
            } else {
                halfHull.remove(currNode);
            }
            currNode = nextNode;
            halfHull.add(nextNode);
        }
        return halfHull;
    }
}
