module MAP.interfaces {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens MAP to javafx.fxml;
    exports MAP;
    exports MAP.interfaces;
    opens MAP.interfaces to javafx.fxml;
}
