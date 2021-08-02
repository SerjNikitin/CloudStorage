package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

public class CloudApp extends Application {
    public static Stream stream=new Stream();
    public static User user = new User();
    public static Stage signInStage;
    public static Stage signUpStage;
    public static FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        CloudApp.signInStage = stage;
        Parent parent = FXMLLoader.load(getClass().getResource("Authorization.fxml"));
        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        stage.show();
    }
}
