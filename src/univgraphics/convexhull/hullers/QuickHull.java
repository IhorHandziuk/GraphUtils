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
public class QuickHull extends Huller {
    public QuickHull(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;

        List<Point> convexHull = new ArrayList<>();
        Node leftMostNode = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(null);
        Node rightMostNode = graph
                .stream()
                .max(Comparator.comparingInt(Point::getX))
                .orElse(null);

        List<Point> leftSideSet = new ArrayList<>();
        List<Point> rightSideSet = new ArrayList<>();
        Edge pivotEdge = new Edge(leftMostNode, rightMostNode);
        graph.remove(leftMostNode);
        graph.remove(rightMostNode);
        for (Node p : graph) {
            if (pivotEdge.pointIsOnRightSide(p)) {
                rightSideSet.add(p);
            } else {
                leftSideSet.add(p);
            }
        }
        graph.add(leftMostNode);
        graph.add(rightMostNode);
        convexHull.add(leftMostNode);
        convexHull.addAll(quickHull(leftMostNode, rightMostNode, rightSideSet));
        convexHull.add(rightMostNode);
        convexHull.addAll(quickHull(rightMostNode, leftMostNode, leftSideSet));

        return convexHull;
    }

    private List<Point> quickHull(Point start, Point end, List<Point> points) {
        if (points.isEmpty()) return points;
        Edge e = new Edge(start, end);
        Point mostFar = points
                .stream()
                .max((o1, o2) -> (int)(e.distance(o1) - e.distance(o2)))
                .orElse(points.iterator().next());
        Edge startToFar = new Edge(start, mostFar);
        Edge farToEnd = new Edge(mostFar, end);
        List<Point> startToFarRight = new ArrayList<>();
        List<Point> farToEndRight = new ArrayList<>();
        for (Point p : points) {
            if (startToFar.pointIsOnRightSide(p)) {
                startToFarRight.add(p);
            } else if (farToEnd.pointIsOnRightSide(p)){
                farToEndRight.add(p);
            }
        }
        List<Point> res = new ArrayList<>();

        res.addAll(quickHull(start, mostFar, startToFarRight));
        res.add(mostFar);
        res.addAll(quickHull(mostFar, end, farToEndRight));
        return res;
    }
}