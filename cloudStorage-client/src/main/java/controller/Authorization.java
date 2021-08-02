package controller;

import animations.Shake;
import dataBase.RequestDB;
import dataBase.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class Authorization implements Initializable {
    public TextField login;
    public PasswordField password;
    private RequestDB requestDB = new RequestDB();


    public void register(ActionEvent actionEvent) {
        openNewScene("CreateNewAccount.fxml", actionEvent);
    }

    public void entrance(ActionEvent actionEvent) {
        String log = login.getText().trim();
        String pass = password.getText().trim();
        if (log.length() > 0 && pass.length() > 0) {
            Optional<User> user = requestDB.findUser(log, pass);
            if (user.isPresent()) {
                openNewScene("CloudStorage.fxml", actionEvent);
            } else {
                Shake shakeLogin = new Shake(login);
                Shake shakePassword = new Shake(password);
                shakeLogin.play();
                shakePassword.play();
            }
        }
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}