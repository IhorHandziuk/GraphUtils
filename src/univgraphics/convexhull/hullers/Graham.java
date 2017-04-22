package univgraphics.convexhull.hullers;

import univgraphics.common.Huller;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by ihorhandziuk on 20.04.17.
 * All code is free to use and distribute.
 */
public class Graham extends Huller {
    public Graham(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        if (graph.size() < 3) return null;
        Node lowestNode = graph
                .stream()
                .min(Comparator.comparingInt(Point::getY))
                .get();
        graph.sort((o1, o2) -> {
            if (o1.equals(lowestNode)) return Integer.MIN_VALUE;
            if (o2.equals(lowestNode)) return Integer.MAX_VALUE;
            boolean onTheRight = (new Edge(o1, o2)).pointIsOnRightSide(lowestNode);
            return onTheRight ? 1 : -1;
        });
        Stack<Point> convexHull = new Stack<>();
        Point nextToPeek = graph.get(0);
        convexHull.push(nextToPeek);
        convexHull.push(graph.get(1));
        for (int i = 2; i < graph.size(); i++) {
            while (new Edge(graph.get(i), nextToPeek).pointIsOnRightSide(convexHull.peek())) {
                convexHull.pop();
                nextToPeek = convexHull.get(convexHull.size() - 2);
            }
            convexHull.push(graph.get(i));
            nextToPeek = convexHull.get(convexHull.size() - 2);
        }
        List<Point> res = new ArrayList<>();
        res.addAll(convexHull);
        return res;
    }
}
