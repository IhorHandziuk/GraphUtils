package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;
import univgraphics.geomsearch.localizators.Localizator;
import univgraphics.geomsearch.localizators.SimpleLocalizator;

import java.util.ArrayList;
import java.util.List;

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
        return divideAndConquer(graph);
    }

    private static List<Point> divideAndConquer(List<Node> points) {
        if (points.size() < 6) {
            return (new Jarvis(points)).getRegion();
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
        List<Point> firstHull = divideAndConquer(firstHalf);
        List<Point> secondHull = divideAndConquer(secondHalf);
        return uniteConvexHulls(firstHull, secondHull);
    }

    private static List<Point> uniteConvexHulls(List<Point> first, List<Point> second) {
        int innerPointX = (first.get(0).getX() + first.get(1).getX() + first.get(2).getX()) / 3;
        int innerPointY = (first.get(0).getY() + first.get(1).getY() + first.get(2).getY()) / 3;
        Point innerPoint = new Point(innerPointX, innerPointY);
       // Localizator simpleLocalizator = new SimpleLocalizator()
        if (/*inside*/ true) {

        }
        return null;
    }
}
