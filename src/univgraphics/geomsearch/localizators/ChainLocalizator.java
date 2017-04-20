package univgraphics.geomsearch.localizators;

import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.*;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class ChainLocalizator extends Localizator {
    public ChainLocalizator(List<Node> graph, Point pointToLocate) {
        super(graph, pointToLocate);
    }

    @Override
    public List<Point> getRegion() {
        Localizator localizator = new StripLocalizator(graph, pointToLocate);
        return localizator.getRegion();
    }
}
