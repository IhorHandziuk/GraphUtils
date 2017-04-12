package univgraphics.lab1;

import univgraphics.primitives.Edge;
import univgraphics.primitives.Generator;
import univgraphics.primitives.Node;

/**
 * Created by Ihor Handziuk on 10.04.2017.
 * All code is free to use and distribute.
 */
public class SimpleGenerator extends Generator{

    public SimpleGenerator(int x0, int y0, int width, int height) {
        super(x0, y0, width, height);
    }

    public void generate() {
        graph.clear();
        int edgeNum = 3 + (int) (Math.random() * 5);
        Edge firstEdge = Edge.createRandomEdge(x0, y0, width, height);
        Node firstNode = firstEdge.getStartNode();
        Node lastNode = firstEdge.getEndNode();
        graph.add(firstNode);
        graph.add(lastNode);
        for (int i = 1; i < edgeNum;) {
            Edge nextEdge = Edge.createRandomEdge(x0, y0, width, height);
            Node edgeStart = nextEdge.getStartNode();
            Node edgeEnd = nextEdge.getEndNode();
            if (!intersectOther(edgeStart, edgeEnd) &&
                    !intersectOther(lastNode, edgeStart) &&
                    !intersectOther(edgeEnd, firstNode) &&
                    !Edge.intersects(lastNode, edgeStart, edgeEnd, firstNode)) {
                graph.add(edgeStart);
                graph.add(edgeEnd);
                lastNode.adj().add(edgeStart);
                edgeStart.adj().add(lastNode);
                lastNode = edgeEnd;
                i += 2;
            }
        }
        lastNode.adj().add(firstNode);
        firstNode.adj().add(lastNode);
    }
}
