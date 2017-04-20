package univgraphics.convexhull.hullers;

import univgraphics.common.*;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.List;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class DivideAndConquer extends Huller {
    public DivideAndConquer(List<Node> graph) {
        super(graph);
    }

    @Override
    public List<Point> getRegion() {
        return (new Jarvis(graph)).getRegion();
    }
}
