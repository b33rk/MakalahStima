module stima {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens stima to javafx.fxml;

    exports stima;
}
