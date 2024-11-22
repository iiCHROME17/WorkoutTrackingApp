module org.chrome.workouttrackerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens org.chrome.workouttrackerapp to javafx.fxml;
    exports org.chrome.workouttrackerapp;
    exports org.chrome.workouttrackerapp.testbuilds;
    opens org.chrome.workouttrackerapp.testbuilds to javafx.fxml;
}