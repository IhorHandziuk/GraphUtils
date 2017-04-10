package univgraphics.primitives;

import java.util.List;

/**
 * Created by Ihor Handziuk on 09.04.2017.
 * All code is free to use and distribute.
 */
public abstract class Localizator {
    protected List<Node> graph;
    protected Point pointToLocate;

    public abstract List<Edge> locate();

    public Localizator(List<Node> graph, Point pointToLocate) {
        this.graph = graph;
        this.pointToLocate = pointToLocate;
    }
}
