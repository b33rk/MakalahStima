package stima;

import javafx.scene.shape.Circle;
import javafx.scene.Node;

public class DraggableMaker {
    private PrimaryController primaryController;

    DraggableMaker(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    public void makeDraggable(Node node) {
        final Delta dragDelta = new Delta();
    
        node.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = node.getLayoutX() - mouseEvent.getSceneX();
            dragDelta.y = node.getLayoutY() - mouseEvent.getSceneY();
        });
    
        node.setOnMouseDragged(mouseEvent -> {
            double newX = mouseEvent.getSceneX() + dragDelta.x;
            double newY = mouseEvent.getSceneY() + dragDelta.y;
            
            node.setLayoutX(newX);
            node.setLayoutY(newY);
        });
    
        node.setOnMouseReleased(event -> {
            primaryController.markAllCircle();
            primaryController.drawAllLine();
        });
    }
    

    class Delta {
        double x, y;
    }
}