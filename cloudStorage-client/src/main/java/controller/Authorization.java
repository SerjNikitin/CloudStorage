package controller;


import animations.Shake;
import dataBase.RequestDB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import model.AbstractCommand;
import model.AuthenticationResponse;
import model.Message;
import model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Authorization implements Initializable {
    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;
    private RequestDB requestDB = new RequestDB();
    private Stream stream = new Stream();
    private Thread readThread;
    private User user = new User();


    public void register(ActionEvent actionEvent) {
        openNewScene("CreateNewAccount.fxml");

    }

//    public void entrance(ActionEvent actionEvent) {
//        String log = login.getText().trim();
//        String pass = password.getText().trim();
//        if (log.length() > 0 && pass.length() > 0) {
//            Optional<User> user = requestDB.findUser(log, pass);
//            if (user.isPresent()) {
//                openNewScene("CloudStorage.fxml", actionEvent);
//            } else {
//                Shake shakeLogin = new Shake(login);
//                Shake shakePassword = new Shake(password);
//                shakeLogin.play();
//                shakePassword.play();
//            }
//        }
//    }

    public void entrance(ActionEvent actionEvent) {
        String log = login.getText().trim();
        String pass = password.getText().trim();
        if (log.length() > 0 && pass.length() > 0) {
            try {
                stream.getOs().writeObject(new model.Authorization(log, pass));
//                openNewScene("CloudStorage.fxml");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Shake shakeLogin = new Shake(login);
            Shake shakePassword = new Shake(password);
            shakeLogin.play();
            shakePassword.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readThread = new Thread(() -> {
            try {
                while (!readThread.isInterrupted()) {
                    AbstractCommand command = (AbstractCommand) stream.getIs().readObject();
                    log.debug("received: {}", command);
                    switch (command.getType()) {

                        case AUTH_RESPONSE:
                            AuthenticationResponse authResponse = (AuthenticationResponse) command;
                            user.setName(authResponse.getName());
                            user.setLogin(authResponse.getLogin());
                            user.setPassword(authResponse.getPassword());

                            openNewScene("CloudStorage.fxml");
                            readThread.interrupt();
                            break;

                        case SIMPLE_MESSAGE:
                            Message message = (Message) command;
                            if (message.toString().equals("Registration successful")) {
                                Platform.runLater(() -> {
                                    CloudApp.signUpStage.hide();
                                    CloudApp.signInStage.show();
//                                    stream.getSignUpStage().hide();
//                                    stream.getSignInStage().show();
                                });
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                e.printStackTrace();
            }
        });
        readThread.setDaemon(true);
        readThread.start();
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
}