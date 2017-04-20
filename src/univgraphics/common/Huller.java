package univgraphics.common;

import com.sun.istack.internal.NotNull;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Point> edgesToPoints(List<Edge> bounds) {
        if (bounds == null) return null;
        List<Point> hull = new ArrayList<>();
        for (int i = 0; i < bounds.size() - 1; i++) {
            hull.add(Edge.getIntersectionPoint(bounds.get(i), bounds.get(i + 1)));
        }
        hull.add(Edge.getIntersectionPoint(bounds.get(0), bounds.get(bounds.size() - 1)));
        return hull;
    }
}
