package univgraphics.common;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import univgraphics.common.generators.Generator;
import univgraphics.common.primitives.Edge;
import univgraphics.common.primitives.Node;
import univgraphics.common.primitives.Point;

import java.util.List;

/**
 * Created by Ihor Handziuk on 13.04.2017.
 * All code is free to use and distribute.
 */
public abstract class GraphController {
    protected static final int pointR = 6;
    protected Generator generator;
    protected List<Node> graph;
    protected GraphicsContext gc;

    @FXML
    protected Canvas drawCanvas;

    @FXML
    public void initialize() {
        gc = drawCanvas.getGraphicsContext2D();
        drawCanvas.setScaleY(-1); // flip y axis
    }

    protected void generateAndDraw() {
        gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        generator.generate();
        graph = generator.getGraph();
        drawGraph();
    }

    protected void drawGraph() {
        for (Node node : graph) {
            gc.fillOval(node.getX() - pointR / 2, node.getY() - pointR / 2, pointR, pointR);
            for (Node adj : node.adj()) {
                gc.strokeLine(node.getX(), node.getY(), adj.getX(), adj.getY());
            }
        }
    }

    protected void drawEdge(Edge edge) {
        gc.strokeLine(edge.getStart().getX(), edge.getStart().getY(), edge.getEnd().getX(), edge.getEnd().getY());
    }

    protected void drawRegion(List<Point> bounds) {
        if (bounds == null) return;
        double[] polygonX = new double[bounds.size()];
        double[] polygonY = new double[bounds.size()];
        int counter = 0;
        gc.setFill(Color.rgb(255, 0, 0, 0.5));
        for (Point p : bounds) {
            if (p != null) {
                polygonX[counter] = p.getX();
                polygonY[counter] = p.getY();
                counter++;
            }
        }
        gc.fillPolygon(polygonX, polygonY, counter);
        gc.setFill(Color.BLACK);
    }
}
