package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.impl.NetworkSettingImpl;

import java.util.Objects;

public class CloudApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        NetworkSettingImpl.signInStage = stage;
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Authorization.fxml")));
        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        stage.show();
    }
}
