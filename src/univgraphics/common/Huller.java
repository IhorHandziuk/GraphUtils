package univgraphics.common;

import com.sun.istack.internal.NotNull;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public abstract class Huller {
    protected List<Node> graph;

    // should return bounding edges in order of circumvent
    public abstract List<Point> getRegion();

    public Huller(@NotNull List<Node> graph) {
        this.graph = graph;
    }

    protected static List<Point> edgesToPoints(List<Edge> bounds) {
        if (bounds == null) return null;
        List<Point> hull = new ArrayList<>();
        for (int i = 0; i < bounds.size() - 1; i++) {
            hull.add(Edge.getIntersectionPoint(bounds.get(i), bounds.get(i + 1)));
        }
        hull.add(Edge.getIntersectionPoint(bounds.get(0), bounds.get(bounds.size() - 1)));
        return hull;
    }

    /**
     * @param points convex hull represented by list of points
     * @return list of nodes (i. e. points are connected)
     */
    protected static List<Node> hullToNodes(List<Point> points) {
        List<Node> res = new ArrayList<>();
        for (Point point : points) {
            res.add(new Node(point));
        }
        for (int i = 0; i < res.size() - 1; i++) {
            Node curr = res.get(i);
            Node next = res.get(i + 1);
            curr.adj().add(next);
            next.adj().add(curr);
        }
        Node first = res.get(0);
        Node last = res.get(res.size() - 1);
        first.adj().add(last);
        last.adj().add(first);
        return res;
    }

    // Removes chain between 2 nodes (nodes are usually obtained via getSupportingLineNode function)
    protected static void removeChainBetween(Node left, Node right, List<Node> nodes) {
        List<Node> toRemove = nodes
                .stream()
                .filter(x -> new Edge(left, right).pointIsOnRightSide(x))
                .collect(Collectors.toList());
        for (Node node : toRemove) {
            node.adj().clear();
            left.adj().remove(node);
            right.adj().remove(node);
            nodes.remove(node);
        }
    }

    /**
     * @param origin point from which we search supporting line
     * @param nodes collection of points to which we search supporting line
     * @param left is supporting line supposed to be left (false if right)
     * @return node that helds supporting line
     */
    protected static Node getSupportingLineNode (Point origin, List<Node> nodes, boolean left) {
        int currIndex = 0;
        Node res = nodes.get(currIndex);
        for (int i = 0; i < nodes.size();) {
            if (nodes.get(i).equals(res)) {
                i++;
            } else if (left != new Edge(origin, res).pointIsOnRightSide(nodes.get(i))) {
                // if left == true then pointIsOnRightSide should be true and vise versa
                currIndex++;
                res = nodes.get(currIndex);
                i = 0;
            } else {
                i++;
            }
        }
        return res;
    }

    /**
     * Sorts simple polygon in order of clockwise circumvent
     * starting from leftmost node
     */
    protected static void sortPolygonByCircumvent(List<Node> polygon) {
        List<Node> res = new ArrayList<>();
        Node leftmostNode = polygon
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(polygon.get(0));
        Node lowerLeftmostAdj = leftmostNode.adj() // guarantee clockwise circumvent
                .stream()
                .min((o1, o2) -> {
                    // 30 is arbitrary (large enough to make difference more than 1)
                    int rightToOrigin = leftmostNode.getX() + 30;
                    double o1X = new Edge(leftmostNode, o1).valueInX(rightToOrigin);
                    double o2X = new Edge(leftmostNode, o2).valueInX(rightToOrigin);
                    return (int) (o1X - o2X);
                }).get();
        Node prevNode = lowerLeftmostAdj;
        Node currNode = leftmostNode;
        Node nextNode = null;
        res.add(leftmostNode);
        while (nextNode != lowerLeftmostAdj) {
            final Node finalPrevNode = prevNode;
            nextNode = currNode.adj()
                    .stream()
                    .filter(x -> x != finalPrevNode)
                    .findFirst()
                    .get();
            prevNode = currNode;
            currNode = nextNode;
            res.add(nextNode);
        }
        polygon.clear();
        polygon.addAll(res);
    }
}
