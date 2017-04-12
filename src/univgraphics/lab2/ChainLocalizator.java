package univgraphics.lab2;

import univgraphics.primitives.Edge;
import univgraphics.primitives.Localizator;
import univgraphics.primitives.Node;
import univgraphics.primitives.Point;

import java.util.List;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class ChainLocalizator extends Localizator {
    public ChainLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
    }

    @Override
    public List<Edge> locate() {
        Localizator localizator = new StripLocalizator(graph, pointToLocate);
        return localizator.locate();
    }
}
