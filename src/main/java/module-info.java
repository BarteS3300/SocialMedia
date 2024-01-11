module MAP.interfaces {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    exports MAP.interfaces;
    exports MAP.domain;
    exports MAP.business;
    opens MAP.interfaces to javafx.fxml;
    opens MAP.domain to javafx.base;
}
