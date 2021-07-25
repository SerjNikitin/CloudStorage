package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent parent= FXMLLoader.load(getClass().getResource("CloudStorage.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
