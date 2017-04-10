package univgraphics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import univgraphics.primitives.Localizator;
import univgraphics.lab2.PlanarPartition;
import univgraphics.lab2.StripLocalizator;
import univgraphics.primitives.Edge;
import univgraphics.primitives.Node;
import univgraphics.primitives.Point;

import java.util.*;

public class Controller {

    @FXML
    private Button locateBtn;

    @FXML
    private Button removePointBtn;

    @FXML
    private Canvas drawCanvas;

    @FXML
    private Button generateBtn;

    private PlanarPartition planarPartition;
    private GraphicsContext gc;
    private List<Node> graph;
    private Point pointToLocate;
    private static final int pointR = 6;

    @FXML
    public void initialize() {
        planarPartition = new PlanarPartition(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        gc = drawCanvas.getGraphicsContext2D();
        drawCanvas.setScaleY(-1); // flip y axis
    }

    @FXML
    void onDrawCanvasClicked(MouseEvent event) {
        if (pointToLocate == null) {
            gc.save();
            pointToLocate = new Point((int) event.getX(), (int) event.getY());
            gc.fillOval(pointToLocate.getX() - pointR / 2, pointToLocate.getY() - pointR / 2, pointR, pointR);
        }
    }

    @FXML
    void onGenerateBtnClick(ActionEvent event) {
        gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        pointToLocate = null;
        planarPartition.generate();

        graph = planarPartition.getGraph();
        drawGraph();
    }

    @FXML
    void onRemovePointBtnClicked(ActionEvent event) {
        if (pointToLocate != null) {
            gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
            pointToLocate = null;
            if (graph != null) {
                drawGraph();
            }
        }
    }

    @FXML
    void onLocateBtnClick(ActionEvent event) {
        if (graph != null && pointToLocate != null) {
            Localizator localizator = new StripLocalizator(graph, pointToLocate);
            List<Edge> bounds = localizator.locate();
            drawRegion(bounds);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Partition is not created or there is no point to locate",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void drawGraph() {
        for (Node node : graph) {
            gc.fillOval(node.getX() - pointR / 2, node.getY() - pointR / 2, pointR, pointR);
            for (Node adj : node.adj()) {
                gc.strokeLine(node.getX(), node.getY(), adj.getX(), adj.getY());
            }
        }
    }

    private void drawEdge(Edge edge) {
        gc.strokeLine(edge.getStart().getX(), edge.getStart().getY(), edge.getEnd().getX(), edge.getEnd().getY());
    }

    private void drawRegion(List<Edge> bounds) {
        if (bounds == null) return;
        Edge leftEdge = bounds.get(0);
        Edge rightEdge = bounds.get(1);
        Edge topEdge = bounds.get(2);
        Edge bottomEdge = bounds.get(3);

        Point[] trapeze = new Point[4];
        trapeze[0] = Edge.getIntersectionPointHorizon(leftEdge, topEdge);
        trapeze[1] = Edge.getIntersectionPointHorizon(rightEdge, topEdge);
        trapeze[3] = Edge.getIntersectionPointHorizon(leftEdge, bottomEdge);
        trapeze[2] = Edge.getIntersectionPointHorizon(rightEdge, bottomEdge);

        gc.setStroke(Color.RED);
        drawEdge(topEdge);
        drawEdge(bottomEdge);
        gc.setStroke(Color.BLACK);

        double[] polygonX = new double[4];
        double[] polygonY = new double[4];
        int counter = 0;
        gc.setFill(Color.rgb(255, 0, 0, 0.5));
        for (Point p : trapeze) {
            if (p != null) {
                polygonX[counter] = p.getX();
                polygonY[counter] = p.getY();
                counter++;
            }
        }
        gc.fillPolygon(polygonX, polygonY, counter);

        gc.setFill(Color.BLACK);
        gc.fillOval(pointToLocate.getX() - pointR / 2, pointToLocate.getY() - pointR / 2, pointR, pointR);
    }
}
