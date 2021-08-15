package controller;

import factory.Factory;
import animations.Shake;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.NetworkSettings;
import service.impl.NetworkSettingImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Authorization implements Initializable {

    public TextField login;
    public PasswordField password;
    public AnchorPane anchorPane;
    private Thread readThread;
    private User user = new User();
//    private Stage signUpStage = new Stage();
//    private final Stage signInStage = new Stage();
//    public FXMLLoader loader = new FXMLLoader();


    public void register(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
//        if (signUpStage == null) {
//            signUpStage = openNewScene("CreateNewAccount.fxml", "");
//        }
        if (NetworkSettingImpl.signUpStage == null) {
            NetworkSettingImpl.signUpStage = openNewScene("CreateNewAccount.fxml", "");
        }
        NetworkSettingImpl.signUpStage.show();
//        signUpStage.show();
    }

    public void entrance(ActionEvent actionEvent) {
        String loginText = login.getText().trim();
        String passText = password.getText().trim();
        if (!loginText.equals("") && !passText.equals("")) {
            try {
                Factory.getNetworkService().sendCommand(new AuthorizationRequest(loginText, passText));
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
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
                    AbstractCommand command = Factory.getNetworkService().readCommandResult();
                    log.debug("received: {}", command);
                    switch (command.getType()) {

                        case AUTH_RESPONSE:
                            AuthenticationResponse authResponse = (AuthenticationResponse) command;

                            user.setName(authResponse.getName());
                            user.setLogin(authResponse.getLogin());
                            user.setPassword(authResponse.getPassword());
//                            NetworkSettingImpl.user.setName(authResponse.getName());
//                            NetworkSettingImpl.user.setLogin(authResponse.getLogin());
//                            NetworkSettingImpl.user.setPassword(authResponse.getPassword());
                            switchToCloud();
                            readThread.interrupt();
                            break;

                        case SIMPLE_MESSAGE:
                            SimpleMessage message = (SimpleMessage) command;
                            if (message.toString().equals("Registration successful")) {
                                Platform.runLater(() -> {
                                    NetworkSettingImpl.signUpStage.hide();
                                    NetworkSettingImpl.signInStage.show();
//                                    signUpStage.hide();
//                                    signInStage.show();
                                });
                            }
                            Platform.runLater(() -> {
                                NetworkSettingImpl.loader.getController();
//                                loader.getController();
                            });
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

//    private Stage openNewScene(String scene, String userName) {
//
////        Stage stage = new Stage();
//        Stage stage = (Stage) anchorPane.getScene().getWindow();
//
//        try {
//            Parent parent = FXMLLoader.load(getClass().getResource(scene));
//            stage.setScene(new Scene(parent));
//            stage.show();
////            anchorPane.getScene().getWindow().hide();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (!userName.equals("")) stage.setTitle("Cloud storage. User: " + userName);
//        else stage.setTitle("Cloud storage");
//        stage.setResizable(false);
//        return stage;
//    }

    private Stage openNewScene(String sceneName, String userName) {
        NetworkSettingImpl.loader  = new FXMLLoader();
        NetworkSettingImpl.loader.setLocation(getClass().getResource(sceneName));
//        loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource(sceneName));
        try {
            NetworkSettingImpl.loader.load();
//            loader.load();
        } catch (IOException e) {
            log.debug("Error scene loading: {}", e.getClass());
        }
        Parent root = NetworkSettingImpl.loader.getRoot();

//        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        if (!userName.equals("")) stage.setTitle("Cloud storage. User: " + userName);
        else stage.setTitle("Cloud storage");
        stage.setResizable(false);
        return stage;
    }

    private void switchToCloud() {
        Platform.runLater(() -> {
            try {
                readThread.join();
            } catch (InterruptedException e) {
                log.error("Error: {}", e.getMessage());
            }
            anchorPane.getScene().getWindow().hide();
            openNewScene("CloudStorage.fxml", user.getLogin()).showAndWait();

//            openNewScene("CloudStorage.fxml", NetworkSettingImpl.user.getLogin()).showAndWait();
        });
    }
}