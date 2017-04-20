package univgraphics.geomsearch.localizators;

import univgraphics.common.*;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 09.04.2017.
 * All code is free to use and distribute.
 */
public class StripLocalizator extends Localizator {

    private final int leftmostX, rightmostX;

    public StripLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
        leftmostX = graph
                .stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(graph.get(0))
                .getX();
        rightmostX = graph
                .stream()
                .max(Comparator.comparingInt(Point::getX))
                .orElse(graph.get(0))
                .getX();
    }

    @Override
    public List<Point> getRegion() {
        List<List<Edge>> sets = computeSetsOfEdges();
        int stripIndex = getStripIndex();
        if (stripIndex < 1) return null;
        int yTop = graph.get(stripIndex).getY();
        int yBottom = graph.get(stripIndex - 1).getY();
        List<Edge> sortedEdgeList = sets.get(stripIndex);

        sortedEdgeList.sort((o1, o2) -> {
            int o1TopX = (int)o1.valueInY(yTop);
            int o2TopX = (int)o2.valueInY(yTop);
            if (o1TopX != o2TopX) {
                return o1TopX - o2TopX;
            } else {
                int o1BottomX = (int)o1.valueInY(yBottom);
                int o2BottomX = (int)o2.valueInY(yBottom);
                return o1BottomX - o2BottomX;
            }
        });
        int edgeIndex = getEdgeIndex(sortedEdgeList);
        if (edgeIndex == -1) return null;

        Edge leftEdge = sortedEdgeList.get(edgeIndex);
        Edge topEdge = new Edge(new Point(leftmostX, yTop), new Point(rightmostX, yTop));
        Edge rightEdge = sortedEdgeList.get(edgeIndex + 1);
        Edge bottomEdge = new Edge(new Point(leftmostX, yBottom), new Point(rightmostX, yBottom));

        return Huller.edgesToPoints(Arrays.asList(leftEdge, topEdge, rightEdge, bottomEdge));
    }

    /*
     * NOTE: edges should be sorted
     * performs binary search on edges
     */
    private int getEdgeIndex(List<Edge> sortedEdges) {
        if (sortedEdges.size() < 2) {
            return -1;
        }
        int index = -1;
        int start = 0;
        int end = sortedEdges.size() - 1;
        while (end != start) {
            if (sortedEdges.get(end).substitutionX(pointToLocate) > 0) {
                return -1;
            }
            Edge left = sortedEdges.get(start);
            Edge right = sortedEdges.get(start + 1);
            int d1 = left.substitutionX(pointToLocate);
            int d2 = right.substitutionX(pointToLocate);
            if (d1 * d2 <= 0) {
                index = start;
                break;
            }
            int mid = (end + start) / 2;
            if (sortedEdges.get(mid).substitutionX(pointToLocate) > 0) {
                start = mid;
            } else if (sortedEdges.get(mid).substitutionX(pointToLocate) < 0) {
                end = mid;
            } else {
                index = mid;
                break;
            }
        }
        return index;
    }

    /*
     * NOTE: graph should be sorted
     * performs binary search on strips
     */
    private int getStripIndex() {
        int index = 0;
        int start = 0;
        int end = graph.size() - 1;
        while (end != start) {
            if (pointToLocate.getY() > graph.get(end).getY()) {
                return -1;
            }
            if (graph.get(start).getY() <= pointToLocate.getY() &&
                   pointToLocate.getY() <= graph.get(start + 1).getY()) {
                return start + 1;
            } else if (graph.get(end - 1).getY() <= pointToLocate.getY() &&
                            pointToLocate.getY() <= graph.get(end).getY()) {
                return end;
            }
            int mid = (end + start) / 2;
            if (pointToLocate.getY() > graph.get(mid).getY()) {
                start = mid;
            } else if (pointToLocate.getY() < graph.get(mid).getY()) {
                end = mid;
            } else {
                return mid;
            }
        }
        return index;
    }

    // ATTENTION: sorts the graph
    private List<List<Edge>> computeSetsOfEdges() {
        Set<Edge> allEdges = new HashSet<>();
        for (Node n : graph) {
            for (Node adj : n.adj()) {
                allEdges.add(new Edge(n, adj));
            }
        }

        List<List<Edge>> sets = new LinkedList<>();
        for (int i = 0; i < graph.size(); i++) {
            sets.add(new ArrayList<>());
        }

        graph.sort((o1, o2) ->
                o1.getY() != o2.getY() ?
                o1.getY() - o2.getY() :
                o1.getX() - o2.getX());

        for (int i = 1; i < graph.size(); i++) {
            // horizontal line that 1 unit further that canvas bounds
            Node left = new Node(leftmostX - 1, graph.get(i - 1).getY());
            Node right = new Node(rightmostX + 1, graph.get(i - 1).getY());
            Edge horizonEdge = new Edge(left, right);
            for (Edge edge : allEdges) {
                if (Edge.intersects(horizonEdge, edge)) {
                    sets.get(i).add(edge);
                }
            }
        }
        return sets;
    }
}
