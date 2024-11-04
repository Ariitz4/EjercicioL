package es.aritzherrero.ejerciciol;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ejercicioL_Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("EJERCICIO L");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/avion.png")));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        Application.launch();
    }
}