package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class Jarvis extends Huller {
    public Jarvis(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;
        final int arbitraryNumber = 100; // should be larger than pi
        List<Edge> bounds = new ArrayList<>();
        Node minXPoint = Collections.min(graph, Comparator.comparingInt(Point::getY));
        Node pointOnHull = minXPoint;
        Node leftPoint = new Node(pointOnHull.getX() - arbitraryNumber, pointOnHull.getY());
        float minAngel = arbitraryNumber;
        Node pointWithMinAngel = null;
        do {
            for (Node it : graph) {
                if (getAngle(pointOnHull, leftPoint, it) < minAngel) {
                    minAngel = getAngle(pointOnHull, leftPoint, it);
                    pointWithMinAngel = it;
                }
            }
            Edge v = new Edge(pointOnHull, pointWithMinAngel);
            bounds.add(v);
            leftPoint = pointOnHull;
            pointOnHull = pointWithMinAngel;
            minAngel = arbitraryNumber;
        } while (pointOnHull != minXPoint);

        return Huller.edgesToPoints(bounds);
    }

    private float getAngle(Point vectorBegin, Point vectorEnd, Point point) {
        Edge vecF = new Edge(vectorBegin, vectorEnd);
        Edge vecS = new Edge(point, vectorBegin);
        return (float)Math.acos(scalarProduct(vecF, vecS));
    }

    // this function represents Edges as Vectors,
    // so should not be moved inside Edge class
    private static float scalarProduct(Edge e1, Edge e2) {
        float e1X = e1.getEnd().getX() - e1.getStart().getX();
        float e1Y = e1.getEnd().getY() - e1.getStart().getY();
        float e2X = e2.getEnd().getX() - e2.getStart().getX();
        float e2Y = e2.getEnd().getY() - e2.getStart().getY();
        float e1Mod = (float)Math.sqrt(e1X * e1X + e1Y * e1Y);
        float e2Mod = (float)Math.sqrt(e2X * e2X + e2Y * e2Y);
        return (e1X * e2X + e1Y * e2Y) / (e1Mod * e2Mod);
    }
}
