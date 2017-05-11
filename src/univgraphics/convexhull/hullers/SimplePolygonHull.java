package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class SimplePolygonHull extends Huller {

    private Node leftmostNode, rightmostNode;
    private final int largestX, largestY;

    public SimplePolygonHull(List<Node> graph) {
        super(graph);
        leftmostNode = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .get();
        rightmostNode = graph
                .stream()
                .max(Comparator.comparingInt(Point::getX))
                .get();
        largestY = graph
                .stream()
                .max(Comparator.comparingInt(Point::getY))
                .get()
                .getY();
        largestX = rightmostNode.getX();
        sortPolygonByCircumvent(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;

        List<Node> upperChain = new ArrayList<>();
        List<Node> lowerChain = new ArrayList<>();
        boolean upper = true;
        for (Node node : graph) {
            if (upper) {
                upperChain.add(node);
            } else {
                lowerChain.add(node);
            }
            if (node == rightmostNode) {
                upper = false;
            }
        }

        List<Point> convexHull = new ArrayList<>();
        Stack<Point> upperPart = getChainHull(upperChain);
        convexHull.addAll(upperPart);

        lowerChain.add(0, rightmostNode);
        lowerChain.add(leftmostNode);

        flipNodesInChain(lowerChain);
        Stack<Point> lowerPart = getChainHull(lowerChain);
        // flip dummy node coordinates
        lowerPart.get(0).setY(largestY - lowerPart.get(0).getY());
        lowerPart.get(0).setX(largestX - lowerPart.get(0).getX());
        flipNodesInChain(lowerChain);

        convexHull.addAll(lowerPart);
        return convexHull;
    }

    private void flipNodesInChain(List<Node> chain) {
        for (Node node : chain) {
            node.setY(largestY - node.getY());
            node.setX(largestX - node.getX());
        }
        Node temp = leftmostNode;
        leftmostNode = rightmostNode;
        rightmostNode = temp;
    }

    private Stack<Point> getChainHull(List<Node> chain) {
        Stack<Node> halfHull = new Stack<>();
        Node first = chain.get(0);

        // 4 is arbitrary number
        Node dummyNode = new Node(first.getX(), first.getY() - 4);
        Node nextToPeek;
        halfHull.push(dummyNode);
        halfHull.push(first);

        for (int i = 1; i < chain.size();) {
            nextToPeek = halfHull.get(halfHull.size() - 2);
            Node currNode = chain.get(i);

            Edge checkRightTurn = new Edge(nextToPeek, currNode);
            if (!checkRightTurn.pointIsOnRightSide(halfHull.peek())) {
                Edge checkLastPoint = new Edge(rightmostNode, halfHull.peek());
                if (checkLastPoint.pointIsOnRightSide(currNode) || currNode.equals(rightmostNode)) {
                    halfHull.push(currNode);
                }
                i++;
            } else {
                halfHull.pop();
            }
        }
        Stack<Point> res = new Stack<>();
        res.addAll(halfHull);
        return res;
    }
}
