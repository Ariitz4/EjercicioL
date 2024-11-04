module es.aritzherrero.ejerciciol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens es.aritzherrero.ejerciciol to javafx.fxml;
    exports es.aritzherrero.ejerciciol;
}