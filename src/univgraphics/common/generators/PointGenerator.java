package univgraphics.common.generators;

import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public class PointGenerator extends Generator {
    public PointGenerator(int x0, int y0, int width, int height) {
        super(x0, y0, width, height);
    }

    @Override
    public void generate() {
        graph.clear();
        int verNum = 4 + (int) (Math.random() * 20);
        for (int i = 0; i < verNum; i++) {
            Point randomPoint = Point.createRandomPoint(x0, y0, width, height);
            Node nextNode = new Node(randomPoint.getX(), randomPoint.getY());
            graph.add(nextNode);
        }
    }
}
