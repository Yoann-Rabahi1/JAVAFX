module org.example.projetjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.projetjavafx to javafx.fxml;
    exports org.example.projetjavafx;
}