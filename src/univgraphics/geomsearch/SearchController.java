package univgraphics.geomsearch;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import univgraphics.common.GraphController;
import univgraphics.common.generators.SimpleGenerator;
import univgraphics.common.primitives.Point;
import univgraphics.geomsearch.localizators.SimpleLocalizator;
import univgraphics.geomsearch.localizators.ChainLocalizator;
import univgraphics.common.generators.PlanarGenerator;
import univgraphics.geomsearch.localizators.Localizator;
import univgraphics.geomsearch.localizators.StripLocalizator;

import java.util.*;

public class SearchController extends GraphController {

    @FXML
    private Button locateSimpleBtn;

    @FXML
    private Button locateChainBtn;

    @FXML
    private Button locateStripBtn;

    @FXML
    private Button generatePlanarPartitionBtn;

    @FXML
    private Button generateSimplePolygonBtn;


    private Point pointToLocate;
    private boolean graphIsSimplePolygon = false;

    // for RegionTree
    private RegionTree regionTree;
    private Point startCorner, endCorner;
    private boolean isRightButtonPressed = false;
    private boolean isLeftButtonPressed = false;

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
        } else if (event.isPrimaryButtonDown()) {
            isLeftButtonPressed = true;
        }
    }

    @FXML
    void onDrawCanvasReleased(MouseEvent event) {
        if (graph != null && isRightButtonPressed) {
            isRightButtonPressed = false;
            endCorner = new Point((int) event.getX(), (int) event.getY());
            drawRectLocalization();
        }
    }

    @FXML
    void onGeneratePlanarPartitionBtnClicked(ActionEvent event) {
        generator = new PlanarGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        pointToLocate = null;
        generateAndDraw();
        graphIsSimplePolygon = false;
    }

    @FXML
    void onGenerateSimplePolygonBtnClicked(ActionEvent event) {
        generator = new SimpleGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        pointToLocate = null;
        generateAndDraw();
        graphIsSimplePolygon = true;
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
            List<Point> bounds = localizator.getRegion();
            drawRegion(bounds);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Partition is not created or there is no point to getRegion",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void drawRectLocalization() {
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
