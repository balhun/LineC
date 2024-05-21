module com.example.linec {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.linec to javafx.fxml;
    exports com.example.linec;
}