package controller;

import dataBase.RequestDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateNewAccount {
    public TextField name;
    public TextField login;
    public PasswordField password;
    private RequestDB requestDB = new RequestDB();

    public void register(ActionEvent actionEvent) {
        String nam = name.getText().trim();
        String log = login.getText().trim();
        String pass = password.getText().trim();
        if (nam.length() > 0 && log.length() > 0 && pass.length() > 0) {
            requestDB.createUser(nam, log, pass);
            openNewScene("CloudStorage.fxml", actionEvent);
        }


//        else сделать реакцию на незаполненные поля


    }

    public void exit(ActionEvent actionEvent) {
        openNewScene("Authorization.fxml", actionEvent);
    }

    private void openNewScene(String scene, ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(scene));
            stage.setScene(new Scene(parent));
            stage.show();
            Button source = (Button) actionEvent.getSource();
            source.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
