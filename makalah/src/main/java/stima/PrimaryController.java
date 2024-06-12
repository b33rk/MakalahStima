package stima;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
// import javafx.util.Pair;
import javafx.scene.text.Text;

public class PrimaryController implements Initializable {

    @FXML
    private HBox HBox;

    @FXML
    private VBox VBox;

    @FXML
    private Button addCustom;

    @FXML
    private Button addRectangle;

    @FXML
    private GridPane mainGrid;

    @FXML
    private Pane mainPane; // A Pane to hold the GridPane and the Lines

    @FXML
    private Button okButton;

    @FXML
    private TextField setRadius;

    @FXML
    private Button ZoomIn;

    @FXML
    private Button ZoomOut;

    @FXML
    private ChoiceBox<String> ChoiceBox;
    
    @FXML
    private CheckBox isCircleBox;

    @FXML
    private CheckBox isManual;

    @FXML
    private Button drawBtn;

    @FXML
    private Label labelRadius;
    
    private ArrayList<ArrayList<Rectangle>> rectangleMatrix = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private List<List<Boolean>> shapeLocation = new ArrayList<>();
    private DraggableMaker draggableMaker = new DraggableMaker(this);
    private List<List<Rectangle>> listPathRect = new ArrayList<>();
    private int pathCount = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize grid with 30 rows and 30 columns
        initializeGrid(21, 29);

        String st[] = { "Set Obstacle", "Set Path" };
        ChoiceBox.setItems(FXCollections.observableArrayList(st));
        ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == "Set Path") {
                isCircleBox.setVisible(false);
                isManual.setVisible(false);
                drawBtn.setVisible(true);
            } else {
                drawBtn.setVisible(false);
                isManual.setVisible(true);
                isManual.setSelected(true);
                isCircleBox.setVisible(true);
            }
        });
        ChoiceBox.setValue("Set Path");
        listPathRect.add(new ArrayList<>());
        isCircleBox.setOnMouseClicked(event -> {
            labelRadius.setVisible(true);
            setRadius.setVisible(true);
            isManual.setSelected(false);
            setRadius.setText("100");
        });
        isManual.setOnMouseClicked(event -> {
            isCircleBox.setSelected(false);
            labelRadius.setVisible(false);
            setRadius.setVisible(false);
        });
        drawBtn.setOnMouseClicked(event -> {
            drawAllLine();
            listPathRect.add(new ArrayList<>());
            pathCount++;
        });
    }

    private void initializeGrid(int numRows, int numCols) {
        mainGrid.getRowConstraints().clear();
        mainGrid.getColumnConstraints().clear();

        for (int i = 0; i < numRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(30);
            mainGrid.getRowConstraints().add(row);
        }

        for (int i = 0; i < numCols; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(30); // Fixed width for each column
            mainGrid.getColumnConstraints().add(col);
        }

        populateGrid(numRows, numCols);
    }

    private void populateGrid(int numRows, int numCols) {
        mainGrid.getChildren().clear();
        rectangleMatrix.clear();

        for (int row = 0; row < numRows; row++) {
            ArrayList<Rectangle> rowRectangle = new ArrayList<>();
            ArrayList<Boolean> rowLocation = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {
                Rectangle rect = new Rectangle(30, 30, Color.LIGHTGRAY);
                rect.setOnMouseClicked(event -> rectangleClicked(rect));
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(1);
                mainGrid.add(rect, col, row);
                rowRectangle.add(rect);
                rowLocation.add(true);
            }
            rectangleMatrix.add(rowRectangle);
            shapeLocation.add(rowLocation);
        }
    }

    private void rectangleClicked(Rectangle rect) {
        Integer rowIndex = GridPane.getRowIndex(rect);
        Integer colIndex = GridPane.getColumnIndex(rect);
        if (ChoiceBox.getValue() == "Set Path") {
            if (rect.getFill() == Color.BLUE) {
                rect.setFill(Color.LIGHTGRAY);
                int pathNum = getRowInListPath(rect);
                listPathRect.get(pathNum).remove(rect);
                drawAllLine();
            } else {
                listPathRect.get(pathCount - 1).add(rect);
                rect.setFill(Color.BLUE);
            }
        } else {
            if (isCircleBox.isSelected()) {
                addCircle(rect.getLayoutX() + rect.getWidth() / 2, rect.getLayoutY() + rect.getHeight() / 2);
            } else {
                if (rect.getFill() == Color.GRAY) {
                    rect.setFill(Color.LIGHTGRAY);
                    shapeLocation.get(rowIndex).set(colIndex, true);
                } else {
                    rect.setFill(Color.GRAY);
                    shapeLocation.get(rowIndex).set(colIndex, false);
                }
            }
            drawAllLine();
        }
    }


    private int getRowInListPath(Rectangle rect) {
        for (int i = 0; i < listPathRect.size(); i++) {
            if (listPathRect.get(i).indexOf(rect) != -1) {
                return i;
            }
        }
        return 0;
    }

    private void drawLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(2);
        lines.add(line);
        mainPane.getChildren().add(line);
    }

    private void drawLineBetweenRects(Rectangle firstRect, Rectangle secondRect) {
        // Get the coordinates of the centers of the rectangles
        double startX = firstRect.getBoundsInParent().getMinX() + firstRect.getWidth() / 2;
        double startY = firstRect.getBoundsInParent().getMinY() + firstRect.getHeight() / 2;
        double currentX = startX;
        double currentY = startY;
        double nextX;
        double nextY;

        AStar aStar = new AStar(mainGrid.getRowCount(), mainGrid.getColumnCount(), shapeLocation);
        int startRow = GridPane.getRowIndex(firstRect);
        int startCol = GridPane.getColumnIndex(firstRect);
        int endRow = GridPane.getRowIndex(secondRect);
        int endCol = GridPane.getColumnIndex(secondRect);
        List<Node> nodes = aStar.findPath(new Node(startRow, startCol), new Node(endRow, endCol));

        for (int i = 1; i < nodes.size(); i++) {
            Rectangle nextRect = rectangleMatrix.get(nodes.get(i).row).get(nodes.get(i).col);
            nextX = nextRect.getBoundsInParent().getMinX() + nextRect.getWidth() / 2;
            nextY = nextRect.getBoundsInParent().getMinY() + nextRect.getHeight() / 2;
            drawLine(currentX, currentY, nextX, nextY);
            currentX = nextX;
            currentY = nextY;
        }
    }

    public void drawAllLine(){
        removeAllLines();
        for (int i = 0; i < pathCount; i++) {
            List<Rectangle> path = new ArrayList<>(listPathRect.get(i));
            for (int rectNum = 0; rectNum < path.size() - 1; rectNum++) {
                drawLineBetweenRects(path.get(rectNum), path.get(rectNum + 1));
            }
        }
    }

    private void removeAllLines() {
        // Remove all lines from the mainPane
        for (Line line : lines) {
            mainPane.getChildren().remove(line);
        }
        lines.clear();
    }

    public void addRow() {
        int numRows = mainGrid.getRowConstraints().size() + 1;
        int numCols = mainGrid.getColumnConstraints().size();

        RowConstraints row = new RowConstraints();
        row.setPrefHeight(30); // Fixed height for each row
        mainGrid.getRowConstraints().add(row);

        populateGrid(numRows, numCols);
    }

    public void addColumn() {
        int numRows = mainGrid.getRowConstraints().size();
        int numCols = mainGrid.getColumnConstraints().size() + 1;

        ColumnConstraints col = new ColumnConstraints();
        col.setPrefWidth(30); // Fixed width for each column
        mainGrid.getColumnConstraints().add(col);

        populateGrid(numRows, numCols);
    }

    public void addCircle(double centerX, double centerY) {
        double radius = Double.parseDouble(setRadius.getText());

        // Create a new circle at the calculated center position
        Circle circle = new Circle(centerX, centerY, radius, Color.GRAY);

        circle.setOnMouseClicked(event -> {
            mainPane.getChildren().remove(circle);
            circles.remove(circle);
            markAllCircle();
        });
        draggableMaker.makeDraggable(circle);
        circles.add(circle);
        mainPane.getChildren().add(circle); // Add the circle to the mainPane

        markCircle(circle, false);
    }

    public void cleanShapeLocation() {
        for (int row = 0; row < shapeLocation.size(); row++) {
            for (int col = 0; col < shapeLocation.get(0).size(); col++) {
                shapeLocation.get(row).set(col, true);
            }
        }
    }

    public void markAllCircle() {
        cleanShapeLocation();
        synchAllObstacle();
        for (Circle circle : circles) {
            markCircle(circle, false);
        }
    }

    public void markCircle(Circle circle, Boolean isMarked) {
        for (int row = 0; row < rectangleMatrix.size(); row++) {
            for (int col = 0; col < rectangleMatrix.get(row).size(); col++) {
                Rectangle rect = rectangleMatrix.get(row).get(col);
                double rectCenterX = rect.getBoundsInParent().getMinX() + rect.getWidth() / 2;
                double rectCenterY = rect.getBoundsInParent().getMinY() + rect.getHeight() / 2;
                double distance = Math.sqrt(Math.pow(rectCenterX - circle.getCenterX(), 2)
                        + Math.pow(rectCenterY - circle.getCenterY(), 2));
                if (distance <= circle.getRadius()) {
                    shapeLocation.get(row).set(col, isMarked);
                }
            }
        }
    }

    public void synchAllObstacle() {
        Integer rowIndex;
        Integer colIndex;
        for (List<Rectangle> listRect : rectangleMatrix) {
            for (Rectangle rect : listRect) {
                rowIndex = GridPane.getRowIndex(rect);
                colIndex = GridPane.getColumnIndex(rect);
                if (rect.getFill() == Color.GRAY) {
                    shapeLocation.get(rowIndex).set(colIndex, false);
                }
            }

        }
    }
}