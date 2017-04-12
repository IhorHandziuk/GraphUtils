package univgraphics.lab2;

import univgraphics.primitives.Edge;
import univgraphics.primitives.Localizator;
import univgraphics.primitives.Node;
import univgraphics.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class ChainLocalizator extends Localizator {
    public ChainLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
        graph.sort((o1, o2) ->
                o1.getY() != o2.getY() ?
                o1.getY() - o2.getY() :
                o1.getX() - o2.getX());
        Set<Edge> allEdges = new HashSet<>();
        for (Node n : graph) {
            for (Node adj : n.adj()) {
                allEdges.add(new Edge(n, adj));
            }
        }
    }

    static class ChainEdge extends Edge {

        ChainEdge(Point start, Point end) {
            super(start, end);
        }
        int in, out;
    }

    @Override
    public List<Edge> locate() {
        List<Edge> sortedEdges = new ArrayList<>();
        int index = -1;
        int start = 0;
        int end = sortedEdges.size() - 1;
        while (end != start) {
            if (sortedEdges.size() < 2) break;
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
        Localizator localizator = new StripLocalizator(graph, pointToLocate);
        return localizator.locate();
    }

    private List<List<Edge>> balance() {
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
            ChainEdge e = new ChainEdge(graph.get(0), graph.get(i + 1));
            for (Edge edge : allEdges) {
                if (Edge.intersects(e, edge)) {
                    e.in++;
                    sets.get(i).add(edge);
                }
            }
        }
        for (int i = graph.size() - 1; i >= 2; i--) {
            ChainEdge e = new ChainEdge(graph.get(0), graph.get(i + 1));
            for (Edge edge : allEdges) {
                if (Edge.intersects(e, edge)) {
                    e.out++;
                    sets.get(i).add(edge);
                }
            }
        }
        return sets;
    }
}
