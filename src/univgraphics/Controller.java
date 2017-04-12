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
import univgraphics.lab1.SimpleGenerator;
import univgraphics.lab1.SimpleLocalizator;
import univgraphics.lab2.ChainLocalizator;
import univgraphics.lab2.PlanarGenerator;
import univgraphics.lab3.RegionTree;
import univgraphics.primitives.*;
import univgraphics.lab2.StripLocalizator;

import java.util.*;

public class Controller {

    @FXML
    private Button locateSimpleBtn;

    @FXML
    private Button locateChainBtn;

    @FXML
    private Button locateStripBtn;

    @FXML
    private Canvas drawCanvas;

    @FXML
    private Button generatePlanarPartitionBtn;

    @FXML
    private Button generateSimplePolygonBtn;

    private Generator generator;
    private GraphicsContext gc;
    private List<Node> graph;
    private Point pointToLocate;
    private static final int pointR = 6;
    private boolean graphIsSimplePolygon = false;

    // for 3d lab
    private RegionTree regionTree;
    private Point startCorner, endCorner;
    private boolean isRightButtonPressed = false;
    private boolean isLeftButtonPressed = false;

    @FXML
    public void initialize() {
        gc = drawCanvas.getGraphicsContext2D();
        drawCanvas.setScaleY(-1); // flip y axis
    }

    @FXML
    void onDrawCanvasClicked(MouseEvent event) {
        if (isLeftButtonPressed) {
            gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
            isLeftButtonPressed = false;
            if (graph != null) {
                drawGraph();
            }
            pointToLocate = new Point((int) event.getX(), (int) event.getY());
            gc.fillOval(pointToLocate.getX() - pointR / 2, pointToLocate.getY() - pointR / 2, pointR, pointR);
        }
    }

    @FXML
    void onDrawCanvasPressed(MouseEvent event) {
        if (graph != null && event.isSecondaryButtonDown()) {
            gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
            pointToLocate = null;
            drawGraph();
            isRightButtonPressed = true;
            regionTree = new RegionTree(graph);
            startCorner = new Point((int) event.getX(), (int) event.getY());
        }
        if (event.isPrimaryButtonDown()) {
            isLeftButtonPressed = true;
        }
    }

    @FXML
    void onDrawCanvasReleased(MouseEvent event) {
        if (graph != null && isRightButtonPressed) {
            isRightButtonPressed = false;
            endCorner = new Point((int) event.getX(), (int) event.getY());
            int leftX = Math.min(startCorner.getX(), endCorner.getX());
            int rightX = Math.max(startCorner.getX(), endCorner.getX());
            int topY = Math.max(startCorner.getY(), endCorner.getY());
            int bottomY = Math.min(startCorner.getY(), endCorner.getY());
            gc.setFill(Color.rgb(0, 0, 255, 0.5));
            gc.fillRect(leftX, bottomY, rightX - leftX, topY - bottomY);
            gc.setFill(Color.BLACK);
            List<Point> points = regionTree.getPoints(startCorner, endCorner);
            gc.setStroke(Color.RED);
            for (Point p : points) {
                gc.strokeOval(p.getX() - pointR / 2, p.getY() - pointR / 2, pointR, pointR);
            }
            gc.setStroke(Color.BLACK);
        }
    }

    @FXML
    void onGeneratePlanarPartitionBtnClicked(ActionEvent event) {
        generator = new PlanarGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        generate();
        graphIsSimplePolygon = false;
    }

    @FXML
    void onGenerateSimplePolygonBtnClicked(ActionEvent event) {
        generator = new SimpleGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        generate();
        graphIsSimplePolygon = true;
    }

    private void generate() {
        gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        pointToLocate = null;
        generator.generate();

        graph = generator.getGraph();
        drawGraph();
    }

    @FXML
    void onLocateChainBtnClicked(ActionEvent event) {
        locate(new ChainLocalizator(graph, pointToLocate));
    }

    @FXML
    void onLocateStripBtnClicked(ActionEvent event) {
        locate(new StripLocalizator(graph, pointToLocate));
    }

    @FXML
    void onLocateSimpleBtnClicked(ActionEvent event) {
        if (graphIsSimplePolygon) {
            locate(new SimpleLocalizator(graph, pointToLocate));
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Graph is not simple polygon",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void locate(Localizator localizator) {
        if (graph != null && pointToLocate != null) {
            gc.clearRect(0, 0, (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
            drawGraph();
            gc.fillOval(pointToLocate.getX() - pointR / 2, pointToLocate.getY() - pointR / 2, pointR, pointR);
            List<Edge> bounds = localizator.locate();
            drawRegion(bounds);
            if (bounds != null && localizator.getClass() == StripLocalizator.class) {
                Edge topEdge = bounds.get(1);
                Edge bottomEdge = bounds.get(3);
                gc.setStroke(Color.RED);
                drawEdge(topEdge);
                drawEdge(bottomEdge);
                gc.setStroke(Color.BLACK);
            }
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
        List<Point> polygon = new ArrayList<>();
        for (int i = 0; i < bounds.size() - 1; i++) {
            polygon.add(Edge.getIntersectionPoint(bounds.get(i), bounds.get(i + 1)));
        }
        polygon.add(Edge.getIntersectionPoint(bounds.get(0), bounds.get(bounds.size() - 1)));

        double[] polygonX = new double[polygon.size()];
        double[] polygonY = new double[polygon.size()];
        int counter = 0;
        gc.setFill(Color.rgb(255, 0, 0, 0.5));
        for (Point p : polygon) {
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
