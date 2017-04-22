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
}
