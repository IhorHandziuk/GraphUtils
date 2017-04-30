package univgraphics.geomsearch.localizators;

import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class ChainLocalizator extends Localizator {
    static class WeightedEdge extends Edge {
        int weight;

        WeightedEdge(Point start, Point end) {
            super(start, end);
            weight = 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            return (getStart().equals(edge.getStart()) && getEnd().equals(edge.getEnd()))
                    || (getStart().equals(edge.getEnd()) && getEnd().equals(edge.getStart()));
        }
    }

    public ChainLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
        graph.sort((o1, o2) ->
                o1.getY() != o2.getY() ?
                o1.getY() - o2.getY() :
                o1.getX() - o2.getX());
        lowestPoint = graph.get(0);
        highestPoint = graph.get(graph.size() - 1);
    }

    private Point lowestPoint, highestPoint;

    @Override
    public List<Point> getRegion() {

        List<WeightedEdge> weightedEdges = getWeightedEdges(graph);
        List<List<WeightedEdge>> chains = getChainsFromWeightedEdges(weightedEdges);
        int indexOfLeftChain = locatePointBetweenChains(chains, pointToLocate);

        if (indexOfLeftChain == -1) {
            return  null;
        }
        List<WeightedEdge> leftChain = chains.get(indexOfLeftChain);
        List<WeightedEdge> rightChain = chains.get(indexOfLeftChain + 1);
        List<Point> res = new ArrayList<>();
        for (WeightedEdge edge : leftChain) {
            res.add(edge.getStart());
        }
        for (int i = rightChain.size() - 1; i >=0; i--) {
            res.add(rightChain.get(i).getEnd());
        }

        return res;
    }

    private static boolean pointIsOnRightToChain(List<WeightedEdge> chain, Point point) {
        Edge appropriateEdge = null;
        for (WeightedEdge edge : chain) {
            if ((point.getY() >= edge.getStart().getY()) && (point.getY() <= edge.getEnd().getY())) {
                appropriateEdge = edge;
                break;
            }
        }
        return appropriateEdge.pointIsOnRightSide(point);
    }

    private int locatePointBetweenChains(List<List<WeightedEdge>> sortedChains, Point point) {
        if (sortedChains.size() < 2) {
            return -1;
        }
        if (point.getY() > highestPoint.getY() || point.getY() < lowestPoint.getY()) {
            return -1;
        }
        int index = -1;
        int start = 0;
        int end = sortedChains.size() - 1;
        while (end != start) {
            if (pointIsOnRightToChain(sortedChains.get(end), point)) {
                return -1;
            }
            List<WeightedEdge> left = sortedChains.get(start);
            List<WeightedEdge> right = sortedChains.get(start + 1);
            boolean isOnRightToLeftChain = pointIsOnRightToChain(left, point);
            boolean isOnRightToRightChain = pointIsOnRightToChain(right, point);
            if (isOnRightToLeftChain && !isOnRightToRightChain) {
                index = start;
                break;
            }
            int mid = (end + start) / 2;
            if (pointIsOnRightToChain(sortedChains.get(mid), point)) {
                start = mid;
            } else if (!pointIsOnRightToChain(sortedChains.get(mid), point)) {
                end = mid;
            }
        }
        return index;
    }

    private List<List<WeightedEdge>> getChainsFromWeightedEdges(List<WeightedEdge> edges) {
        List<List<WeightedEdge>> res = new LinkedList<>();

        List<WeightedEdge> allEdges = new LinkedList<>();
        allEdges.addAll(edges);

        while (!allEdges.isEmpty()) {
            List<WeightedEdge> currChain = new LinkedList<>();

            WeightedEdge currEdge = allEdges
                    .stream()
                    .filter(edge -> edge.getStart().equals(lowestPoint))
                    .min(edgeComparatorX)
                    .get();

            currChain.add(currEdge);
            currEdge.weight--;
            if (currEdge.weight == 0) {
                allEdges.remove(currEdge);
            }
            while (!currEdge.getEnd().equals(highestPoint)) {
                final WeightedEdge prevEdge = currEdge;
                currEdge = allEdges
                        .stream()
                        .filter(edge -> edge.getStart().equals(prevEdge.getEnd()))
                        .min(edgeComparatorX)
                        .get();
                currEdge.weight--;
                if (currEdge.weight == 0) {
                    allEdges.remove(currEdge);
                }
                currChain.add(currEdge);
            }
            res.add(currChain);
        }
        return res;
    }

    private static List<WeightedEdge> getWeightedEdges(List<Node> graph) {
        List<WeightedEdge> outEdges = new LinkedList<>();
        for (Node origin : graph) {
            for (Node adj : origin.adj()) {
                if (origin.getY() < adj.getY()) {
                    outEdges.add(new WeightedEdge(origin, adj));
                }
            }
        }

        int[] weightsIn = new int [graph.size()];
        int[] weightsOut = new int [graph.size()];

        for (int i = 1; i < graph.size() - 1; i++) {
            int finalI = i;
            weightsIn[i] = outEdges
                    .stream()
                    .filter(x -> x.getEnd().equals(graph.get(finalI)))
                    .mapToInt(o -> o.weight)
                    .sum();
            WeightedEdge leftmostOut = outEdges
                    .stream()
                    .filter(x -> x.getStart().equals(graph.get(finalI)))
                    .min(edgeComparatorX)
                    .get();
            int vOut = (int) outEdges
                    .stream()
                    .filter(x -> x.getStart().equals(graph.get(finalI)))
                    .count();
            if (weightsIn[i] > vOut) {
                leftmostOut.weight = weightsIn[i] - vOut + 1;
                weightsIn[graph.indexOf(leftmostOut.getEndNode())] += leftmostOut.weight - 1;
                weightsOut[i] += leftmostOut.weight - 1;
            }
        }
        for (int i = graph.size() - 2; i > 0; i--) {
            int finalI = i;
            weightsOut[i] = outEdges
                    .stream()
                    .filter(x -> x.getStart().equals(graph.get(finalI)))
                    .mapToInt(o -> o.weight)
                    .sum();
            WeightedEdge leftmostIn = outEdges
                    .stream()
                    .filter(x -> x.getEnd().equals(graph.get(finalI)))
                    .min(edgeComparatorX)
                    .get();
            if (weightsOut[i] > weightsIn[i]) {
                leftmostIn.weight += weightsOut[i] - weightsIn[i];
                weightsIn[i] += leftmostIn.weight - 1;
                weightsOut[graph.indexOf(leftmostIn.getStartNode())] += leftmostIn.weight - 1;
            }
        }
        return outEdges;
    }

    private static Comparator<WeightedEdge> edgeComparatorX = (o1, o2) -> {
        if (o1.getStart().equals(o2.getStart())) {
            int upperY = o1.getStart().getY() + 100; // 100 is arbitrary (but large enough) number
            Edge o1Edge = new Edge(o1.getStart(), o1.getEnd());
            Edge o2Edge = new Edge(o2.getStart(), o2.getEnd());
            return (int) (o1Edge.valueInY(upperY) - o2Edge.valueInY(upperY));
        } else {
            if (o1.getStart().getY() != o2.getStart().getY()) {
                return o1.getStart().getY() - o2.getStart().getY();
            }
            return o1.getStart().getX() - o2.getStart().getX();
        }
    };
}
