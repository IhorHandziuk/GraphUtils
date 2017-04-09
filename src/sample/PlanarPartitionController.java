package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import sample.primitives.Edge;
import sample.primitives.Node;
import sample.primitives.Point;

import java.util.*;

public class PlanarPartitionController {

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
            locate();
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

    private void locate() {
        List<List<Edge>> sets = computeSetsOfEdges();
        int stripIndex = getStripIndex();
        if (stripIndex == -1) return;
        int yTop = graph.get(stripIndex).getY();
        final int yBottom;
        if (stripIndex > 0) {
            yBottom = graph.get(stripIndex - 1).getY();
        } else {
            yBottom = 0;
        }
        List<Edge> sortedEdgeList = sets.get(stripIndex);

        sortedEdgeList.sort((o1, o2) -> {
            int o1TopX = (int)o1.valueInY(yTop);
            int o2TopX = (int)o2.valueInY(yTop);
            if (o1TopX != o2TopX) {
                return o1TopX - o2TopX;
            } else {
                int o1BottomX = (int)o1.valueInY(yBottom);
                int o2BottomX = (int)o2.valueInY(yBottom);
                return o1BottomX - o2BottomX;
            }
        });
        int edgeIndex = getEdgeIndex(sortedEdgeList);
        if (edgeIndex == -1) return;

        // Find bounding points and region red
        gc.setStroke(Color.RED);
        Edge e1 = sortedEdgeList.get(edgeIndex);
        Edge e2 = sortedEdgeList.get(edgeIndex + 1);

        Edge topEdge = new Edge(new Point(0, yTop), new Point((int)drawCanvas.getWidth(), yTop));
        Point[] trapeze = new Point[4];
        trapeze[0] = Edge.getIntersectionPointHorizon(e1, topEdge);
        trapeze[1] = Edge.getIntersectionPointHorizon(e2, topEdge);
        gc.strokeLine(0, yTop, drawCanvas.getWidth(), yTop);
        if (stripIndex > 0) {
            Edge bottomEdge = new Edge(new Point(0, yBottom), new Point((int)drawCanvas.getWidth(), yBottom));
            trapeze[3] = Edge.getIntersectionPointHorizon(e1, bottomEdge);
            trapeze[2] = Edge.getIntersectionPointHorizon(e2, bottomEdge);
            gc.strokeLine(0, yBottom, drawCanvas.getWidth(), yBottom);
        }
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

    /*
     * NOTE: edges should be sorted
     * performs binary search on edges
     */
    private int getEdgeIndex(List<Edge> sortedEdges) {
        if (sortedEdges.size() < 2) {
            return -1;
        }
        int index = -1;
        int start = 0;
        int end = sortedEdges.size() - 1;
        while (end != start) {
            if (sortedEdges.get(end).substitutionX(pointToLocate) > 0) {
                return -1;
            }
            Edge left = sortedEdges.get(start);
            Edge right = sortedEdges.get(start + 1);
            int d1 = left.substitutionX(pointToLocate);
            int d2 = right.substitutionX(pointToLocate);
            if (d1 * d2 <= 0) {
                index = start;
                break;
            }
            int mid = (end + start) / 2;
            if (sortedEdges.get(mid).substitutionX(pointToLocate) > 0) {
                start = mid;
            } else if (sortedEdges.get(mid).substitutionX(pointToLocate) < 0) {
                end = mid;
            } else {
                index = mid;
                break;
            }
        }
        return index;
    }

    /*
     * NOTE: graph should be sorted
     * performs binary search on strips
     */
    private int getStripIndex() {
        int index = 0;
        int start = 0;
        int end = graph.size() - 1;
        while (end != start) {
            if (pointToLocate.getY() > graph.get(end).getY()) {
                return -1;
            }
            if (graph.get(start).getY() <= pointToLocate.getY() && pointToLocate.getY() <= graph.get(start + 1).getY()) {
                return start + 1;
            } else if (graph.get(end - 1).getY() <= pointToLocate.getY() && pointToLocate.getY() <= graph.get(end).getY()) {
                return end;
            }
            int mid = (end + start) / 2;
            if (pointToLocate.getY() > graph.get(mid).getY()) {
                start = mid;
            } else if (pointToLocate.getY() < graph.get(mid).getY()) {
                end = mid;
            } else {
                return mid;
            }
        }
        return index;
    }

    // ATTENTION: sorts the graph
    private List<List<Edge>> computeSetsOfEdges() {
        Set<Edge> allEdges = new HashSet<>();
        for (Node n : graph) {
            for (Node adj : n.adj()) {
                allEdges.add(new Edge(n, adj));
            }
        }

        List<List<Edge>> sets = new LinkedList<>();
        for (int i = 0; i < graph.size(); i++) {
            sets.add(new ArrayList<>());
        }

        graph.sort((o1, o2) -> o1.getY() != o2.getY() ? o1.getY() - o2.getY() : o1.getX() - o2.getX());

        for (int i = 1; i < graph.size(); i++) {
            // horizontal line that 1 unit further that canvas bounds
            Node left = new Node(-1, graph.get(i - 1).getY());
            Node right = new Node((int)drawCanvas.getWidth() + 1, graph.get(i - 1).getY());
            Edge horizonEdge = new Edge(left, right);
            for (Edge edge : allEdges) {
                if (Edge.intersect(horizonEdge, edge)) {
                    sets.get(i).add(edge);
                }
            }
        }
        return sets;
    }
}
