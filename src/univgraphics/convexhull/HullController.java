package univgraphics.convexhull;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import univgraphics.common.GraphController;
import univgraphics.common.generators.PointGenerator;
import univgraphics.convexhull.hullers.DivideAndConquer;
import univgraphics.convexhull.hullers.QuickHull;
import univgraphics.convexhull.hullers.SimplePolygonHull;
import univgraphics.common.generators.SimpleGenerator;

public class HullController extends GraphController{
    @FXML
    private Button generateSimplePolygonBtn;

    @FXML
    private Button generatePointsBtn;

    @FXML
    private Button preparataBtn;

    @FXML
    private Button simplePolygonHullBtn;

    @FXML
    private Canvas drawCanvas;

    @FXML
    private Button divideAndConquerBtn;

    @FXML
    private Button quickHullBtn;

    private boolean isSimplePolygon = false;
    private boolean hullIsDone = false;

    @FXML
    void onGeneratePointsBtnClicked(ActionEvent event) {
        generator = new PointGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        generateAndDraw();
        isSimplePolygon = false;
        hullIsDone = false;
    }

    @FXML
    void onGenerateSimplePolygonBtnClicked(ActionEvent event) {
        generator = new SimpleGenerator(0, 0,
                (int)drawCanvas.getWidth(), (int)drawCanvas.getHeight());
        generateAndDraw();
        isSimplePolygon = true;
        hullIsDone = false;
    }

    @FXML
    void onQuickHullBtnClicked(ActionEvent event) {
       // if (graph != null && !hullIsDone) {
            drawRegion((new QuickHull(graph)).getRegion());
            hullIsDone = true;
       // }
    }

    @FXML
    void onDivideAndConquerBtnClicked(ActionEvent event) {
       // if (graph != null && !hullIsDone) {
            drawRegion((new DivideAndConquer(graph)).getRegion());
            hullIsDone = true;
       // }
    }

    @FXML
    void onSimplePolygonHullBtnClicked(ActionEvent event) {
        //if (graph != null && !hullIsDone) {
            if (isSimplePolygon) {
                drawRegion((new SimplePolygonHull(graph)).getRegion());
                hullIsDone = true;
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "There is no simple polygon",
                        ButtonType.OK);
                alert.showAndWait();
            }
        //}
    }

    @FXML
    void onPreparataBtnClicked(ActionEvent event) {

    }
}
