package com.mycompany.proyectousicalxbdl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static Stage mainStage;

    public static void setScene(Scene scene) {
 
        scene.getStylesheets().add(App.class.getResource("/com/mycompany/proyectousicalxbdl/ventana principal.css").toExternalForm());

       
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                mainStage.setFullScreen(!mainStage.isFullScreen());
            }
        });

        mainStage.setScene(scene);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Scene scene = new Scene(loader.load());
        setScene(scene);

        stage.setTitle("Reproductor");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setFullScreen(true); 
        stage.setFullScreenExitHint(""); 
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
