package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
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
public class Preparata extends Huller {
    public Preparata(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;
        List<Node> workingSetForHull = new ArrayList<>();
        for (Node node : graph) {
            node.adj().clear();
            preparata(workingSetForHull, node);
        }
        sortPolygonByCircumvent(workingSetForHull);
        List<Point> res = new ArrayList<>();
        res.addAll(workingSetForHull);
        return res;
    }

    private static void preparata(List<Node> prevHull, Node newPoint) {
        switch (prevHull.size()) {
            case 2:
                Node second = prevHull.get(1);
                second.adj().add(newPoint);
                newPoint.adj().add(second);
                // there is no break intentionally
            case 1:
                Node first = prevHull.get(0);
                first.adj().add(newPoint);
                newPoint.adj().add(first);
                // there is no break intentionally
            case 0:
                prevHull.add(newPoint);
                break;
            default:
                Localizator simpleLocalizator = new SimpleLocalizator(prevHull, newPoint);
                if (simpleLocalizator.getRegion() == null) {
                    Node left = getSupportingLineNode(newPoint, prevHull, true);
                    Node right = getSupportingLineNode(newPoint, prevHull, false);
                    removeChainBetween(left, right, prevHull);
                    if (new Edge(left, right).pointIsOnRightSide(newPoint)
                            && prevHull.size() > 2) {
                        left.adj().remove(right);
                        right.adj().remove(left);
                    }
                    left.adj().add(newPoint);
                    newPoint.adj().add(left);
                    right.adj().add(newPoint);
                    newPoint.adj().add(right);
                    prevHull.add(newPoint);
                }
        }
    }
}
