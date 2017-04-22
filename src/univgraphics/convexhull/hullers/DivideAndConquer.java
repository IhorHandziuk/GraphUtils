package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;
import univgraphics.geomsearch.localizators.Localizator;
import univgraphics.geomsearch.localizators.SimpleLocalizator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class DivideAndConquer extends Huller {
    public DivideAndConquer(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) {
            return null;
        }
        List<Point> res = new ArrayList<>();
        res.addAll(divideAndConquer(graph));
        return res;
    }

    /**
     * NOTE: hullToNodes function were used here so performance
     * of this method is slightly worse then best possible.
     * This was done for the sake of readability and unified interfaces
     */
    private static List<Node> divideAndConquer(List<Node> points) {
        if (points.size() < 6) { // minimal number of points should not be less than 6
            List<Point> pointHull = (new Jarvis(points)).getRegion();
            return hullToNodes(pointHull);
        }
        List<Node> firstHalf = new ArrayList<>();
        List<Node> secondHalf = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i < points.size() / 2) {
                firstHalf.add(points.get(i));
            } else {
                secondHalf.add(points.get(i));
            }
        }
        List<Node> firstHull = divideAndConquer(firstHalf);
        List<Node> secondHull = divideAndConquer(secondHalf);
        List<Point> pointHull = uniteConvexHulls(firstHull, secondHull);
        return hullToNodes(pointHull);
    }

    private static List<Point> uniteConvexHulls(List<Node> first, List<Node> second) {
        int innerPointX = (first.get(0).getX() + first.get(1).getX() + first.get(2).getX()) / 3;
        int innerPointY = (first.get(0).getY() + first.get(1).getY() + first.get(2).getY()) / 3;
        Point innerPoint = new Point(innerPointX, innerPointY);
        Localizator simpleLocalizator = new SimpleLocalizator(second, innerPoint);
        if (simpleLocalizator.getRegion() == null) {
            Node left = getSupportingLinePoint(innerPoint, second, true);
            Node right = getSupportingLinePoint(innerPoint, second, false);
            removeChainBetween(left, right, second);
        }
        List<Node> unitedList = new ArrayList<>();
        unitedList.addAll(first);
        unitedList.addAll(second);
        return (new Graham(unitedList)).getRegion();
    }

    private static void removeChainBetween(Node left, Node right, List<Node> nodes) {
        List<Node> toRemove = nodes
                .stream()
                .filter(x -> new Edge(left, right).pointIsOnRightSide(x))
                .collect(Collectors.toList());
        for (Node node : toRemove) {
            nodes.remove(node);
        }
    }

    private static Node getSupportingLinePoint(Point origin, List<Node> nodes, boolean left) {
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
}
