package univgraphics.geomsearch.localizators;

import com.sun.istack.internal.NotNull;
import univgraphics.common.Huller;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.List;

/**
 * Created by Ihor Handziuk on 09.04.2017.
 * All code is free to use and distribute.
 */
public abstract class Localizator extends Huller {
    protected Point pointToLocate;

    public Localizator(@NotNull List<Node> graph, Point pointToLocate) {
        super(graph);
        this.pointToLocate = pointToLocate;
    }
}
