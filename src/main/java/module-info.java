module com.mycompany.proyectousicalxbdl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.mycompany.proyectousicalxbdl to javafx.fxml;
    exports com.mycompany.proyectousicalxbdl;
}

