package controller;

import animations.Shake;
import dataBase.RequestDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.RegistrationRequest;

import java.io.IOException;

@Slf4j
public class CreateNewAccount {
    public TextField name;
    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;

    public void register(ActionEvent actionEvent) {
        String nam = name.getText().trim();
        String log = login.getText().trim();
        String pass = password.getText().trim();
        if (!nam.equals("") && !log.equals("") && !pass.equals("")) {
            try {
                NetworkSettings.stream.getOs().writeObject(new RegistrationRequest(nam, log, pass));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Shake shakeName = new Shake(name);
            Shake shakeLogin = new Shake(login);
            Shake shakePassword = new Shake(password);
            shakeName.play();
            shakeLogin.play();
            shakePassword.play();
        }
    }

    private void openNewScene(String scene) {
        Stage stage = new Stage();
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(scene));
            stage.setScene(new Scene(parent));
            stage.show();
            anchorPane.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {
        anchorPane.getScene().getWindow().hide();
        NetworkSettings.signInStage.show();
//        openNewScene("Authorization.fxml");
    }
}
