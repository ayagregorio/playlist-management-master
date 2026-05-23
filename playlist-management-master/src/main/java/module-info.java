module com.example.playlist_management {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens com.example.playlist_management.controller to javafx.fxml;
    opens com.example.playlist_management.model to javafx.base;
    exports com.example.playlist_management.app;
}
